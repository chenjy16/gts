package com.wf.gts.common.beans;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;



/**
 * netty客户端与服务端数据交换对象
 */
@Getter
@Setter
public class HeartBeat implements Serializable {

    private static final long serialVersionUID = 4183978848464761529L;
    /**
     * 执行动作 
     */
    private int action;


    /**
     * 执行发送数据任务task key
     */
    private String key;


    
    private int result;


    /**
     * 事务组信息
     */
    private TransGroup txTransactionGroup;

   
}
