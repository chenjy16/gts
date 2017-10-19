package com.wf.gts.manage.entity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TccErrorResponse implements Serializable {

    private static final long serialVersionUID = 6016973979617189095L;

    private List<Participant> participantLinks;

}
