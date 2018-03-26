package com.wf.gts.common.beans;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TransGroup implements Serializable {


    private static final long serialVersionUID = -8826219545126676832L;

    /**
     * 事务组id
     */
    private String id;

    /**
     * 事务等待时间
     */
    private int waitTime;

    /**
     * 事务状态
     */
    private int status;

    private  List<TransItem> itemList;

   
}
