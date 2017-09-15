package com.wf.gts.common.beans;

import java.io.Serializable;
import java.util.List;

public class TxTransactionGroup implements Serializable {


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
     * 事务状态 {@linkplain com.happylifeplat.transaction.common.enums.TransactionStatusEnum}
     */
    private int status;

    private  List<TxTransactionItem> itemList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<TxTransactionItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TxTransactionItem> itemList) {
        this.itemList = itemList;
    }
}
