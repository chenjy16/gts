package com.wf.gts.manage.entity;
import java.time.OffsetDateTime;

import org.hibernate.validator.constraints.URL;
import org.springframework.http.ResponseEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Participant {

    @URL
    private String uri;

    private OffsetDateTime expireTime;

    private OffsetDateTime executeTime;

    private ResponseEntity<?> participantErrorResponse;

    private TccStatus tccStatus = TccStatus.TO_BE_CONFIRMED;
}
