package com.wf.gts.common.beans;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransItem implements Serializable {

    private static final long serialVersionUID = -983809184773470584L;
    /**
     * taskKey
     */
    private String taskKey;

    /**
     * 参与事务id
     */
    private String transId;

    /**
     * 事务状态
     */
    private int status;
    
    /**
     * 事务状态 value
     */
    private String statusValue;

    /**
     * 事务角色 
     */
    private int role;
    
    /**
     * 事务角色 value
     */
    private String roleValue;

    /**
     * 模块信息
     */
    private String modelName;

    /**
     * tm 的域名信息
     */
    private String tmDomain;


    /**
     * 存放事务组id
     */
    private String txGroupId;



	
}
