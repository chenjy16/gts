package com.wf.gts.core.bean;

import com.wf.gts.core.annotation.TxTransaction;

public class TxTransactionInfo {

    /**
     * 补偿方法对象
     */
    private TransactionInvocation invocation;
    /**
     * 分布式事务组
     */
    private String txGroupId;

    private TxTransaction  txTransaction;

    public TxTransactionInfo(String txGroupId, TransactionInvocation invocation) {
        this.txGroupId = txGroupId;
        this.invocation = invocation;
    }

    public TxTransactionInfo(TxTransaction txTransaction,String txGroupId,TransactionInvocation invocation) {
        this.txTransaction = txTransaction;
        this.txGroupId = txGroupId;
        this.invocation = invocation;
    }


    public TransactionInvocation getInvocation() {
        return invocation;
    }

    public String getTxGroupId() {
        return txGroupId;
    }

    public TxTransaction getTxTransaction() {
      return txTransaction;
    }

    
}
