package com.wf.gts.core.bean;

public class TxTransactionInfo {

    /**
     * 补偿方法对象
     */
    private TransactionInvocation invocation;


    /**
     * 分布式事务组
     */
    private String txGroupId;

    private String compensationId;

    public TxTransactionInfo(String txGroupId, TransactionInvocation invocation) {
        this.txGroupId = txGroupId;
        this.invocation = invocation;
    }

    public TxTransactionInfo(
            String compensationId, String txGroupId,
            TransactionInvocation invocation) {
        this.compensationId = compensationId;
        this.txGroupId = txGroupId;
        this.invocation = invocation;
    }


    public TransactionInvocation getInvocation() {
        return invocation;
    }

    public String getTxGroupId() {
        return txGroupId;
    }


    public String getCompensationId() {
        return compensationId;
    }
}
