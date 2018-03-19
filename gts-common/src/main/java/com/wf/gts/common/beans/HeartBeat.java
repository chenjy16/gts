package com.wf.gts.common.beans;
import java.io.Serializable;



/**
 * netty客户端与服务端数据交换对象
 */
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
    private TxTransactionGroup txTransactionGroup;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public TxTransactionGroup getTxTransactionGroup() {
        return txTransactionGroup;
    }

    public void setTxTransactionGroup(TxTransactionGroup txTransactionGroup) {
        this.txTransactionGroup = txTransactionGroup;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
