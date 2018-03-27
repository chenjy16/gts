package com.wf.gts.core.bean;

import com.wf.gts.common.beans.TransactionInvocation;
import com.wf.gts.core.annotation.GtsTransaction;

import lombok.Getter;
@Getter
public class GtsTransInfo {
    /**
     * 补偿方法对象
     */
    private TransactionInvocation invocation;
    
    /**
     * 分布式事务组
     */
    private String txGroupId;

    private GtsTransaction  txTransaction;

    public GtsTransInfo(String txGroupId, TransactionInvocation invocation) {
        this.txGroupId = txGroupId;
        this.invocation = invocation;
    }

    public GtsTransInfo(GtsTransaction txTransaction,String txGroupId,TransactionInvocation invocation) {
        this.txTransaction = txTransaction;
        this.txGroupId = txGroupId;
        this.invocation = invocation;
    }


    
}
