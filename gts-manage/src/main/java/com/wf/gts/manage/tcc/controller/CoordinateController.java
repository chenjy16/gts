package com.wf.gts.manage.tcc.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wf.gts.manage.entity.TccRequest;
import com.wf.gts.manage.tcc.service.CoordinateService;



@RestController
@RequestMapping(value = "/tcc", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class CoordinateController {
  
    private static final String COORDINATOR_URI_PREFIX = "/coordinator";
    
    @Autowired
    private CoordinateService tccService;
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = COORDINATOR_URI_PREFIX + "/confirm", method = RequestMethod.PUT)
    public void confirm(@Valid @RequestBody TccRequest request, BindingResult result) {
        tccService.confirm(request);
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = COORDINATOR_URI_PREFIX + "/cancel", method = RequestMethod.PUT)
    public void cancel(@Valid @RequestBody TccRequest request, BindingResult result) {
        tccService.cancel(request);
    }

}
