package com.wf.gts.common.beans;
import java.io.Serializable;


public class TxTransactionItem implements Serializable {

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
     * 事务角色 
     */
    private int role;

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




    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }


    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getTmDomain() {
        return tmDomain;
    }

    public void setTmDomain(String tmDomain) {
        this.tmDomain = tmDomain;
    }

    public String getTxGroupId() {
        return txGroupId;
    }

    public void setTxGroupId(String txGroupId) {
        this.txGroupId = txGroupId;
    }
}
