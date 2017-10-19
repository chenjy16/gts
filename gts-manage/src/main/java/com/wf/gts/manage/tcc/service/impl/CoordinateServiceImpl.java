package com.wf.gts.manage.tcc.service.impl;
import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.google.common.collect.ImmutableList;
import com.wf.gts.manage.entity.Participant;
import com.wf.gts.manage.entity.TccErrorResponse;
import com.wf.gts.manage.entity.TccRequest;
import com.wf.gts.manage.entity.TccStatus;
import com.wf.gts.manage.tcc.exception.ConfirmException;
import com.wf.gts.manage.tcc.service.CoordinateService;

@Service
public class CoordinateServiceImpl implements CoordinateService{
  
  private static final Logger LOGGER = LoggerFactory.getLogger(CoordinateServiceImpl.class);
  private final RestTemplate restTemplate;
  private static final HttpEntity<?> REQUEST_ENTITY;

  static {
      final HttpHeaders header = new HttpHeaders();
      header.setAccept(ImmutableList.of(MediaType.APPLICATION_JSON_UTF8));
      header.setContentType(MediaType.APPLICATION_JSON_UTF8);
      REQUEST_ENTITY = new HttpEntity<>(header);
  }

  @Autowired
  public CoordinateServiceImpl(RestTemplate restTemplate) {
      this.restTemplate = restTemplate;
  }

  public void confirm(TccRequest request) {
      final List<Participant> participantLinks = request.getParticipantLinks();
      int success = 0;
      int fail = 0;
      for (Participant participant : participantLinks) {
          participant.setExecuteTime(OffsetDateTime.now());
          final ResponseEntity<String> response = restTemplate.exchange(participant.getUri(), HttpMethod.PUT, REQUEST_ENTITY, String.class);
          if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
              participant.setTccStatus(TccStatus.CONFIRMED);
              success++;
          } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
              participant.setTccStatus(TccStatus.TIMEOUT);
              participant.setParticipantErrorResponse(response);
              fail++;
          }
      }
      // 检查是否有冲突
      if (success > 0 && fail > 0) {
          throw new ConfirmException("出现冲突必须返回并需要人工介入", new TccErrorResponse(participantLinks));
      }
      if(participantLinks.size()==fail){
          throw new ConfirmException("确认失败",new TccErrorResponse(participantLinks));
      }
  }


  public void cancel(TccRequest request) {
      final List<Participant> participantList = request.getParticipantLinks();
      try {
        participantList.forEach(participant->{
            restTemplate.exchange(participant.getUri(), HttpMethod.DELETE, null, String.class);
        });
      } catch (Exception e) {
          LOGGER.debug("unexpected error when making compensation: {}", e.toString());
      }
  }
}
