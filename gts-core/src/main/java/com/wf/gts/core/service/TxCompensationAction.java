package com.wf.gts.core.service;
import java.io.Serializable;
import com.wf.gts.common.enums.CompensationActionEnum;
import com.wf.gts.core.bean.TransactionRecover;

public class TxCompensationAction implements Serializable {

    private static final long serialVersionUID = 7474184793963072848L;


    private CompensationActionEnum compensationActionEnum;


    private TransactionRecover transactionRecover;

    public CompensationActionEnum getCompensationActionEnum() {
        return compensationActionEnum;
    }

    public void setCompensationActionEnum(CompensationActionEnum compensationActionEnum) {
        this.compensationActionEnum = compensationActionEnum;
    }

    public TransactionRecover getTransactionRecover() {
        return transactionRecover;
    }

    public void setTransactionRecover(TransactionRecover transactionRecover) {
        this.transactionRecover = transactionRecover;
    }


}
