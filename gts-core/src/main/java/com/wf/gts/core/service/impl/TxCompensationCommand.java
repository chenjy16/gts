package com.wf.gts.core.service.impl;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wf.gts.common.enums.CompensationActionEnum;
import com.wf.gts.common.enums.TransactionStatusEnum;
import com.wf.gts.common.utils.IdWorkerUtils;
import com.wf.gts.core.bean.TransactionInvocation;
import com.wf.gts.core.bean.TransactionRecover;
import com.wf.gts.core.service.Command;
import com.wf.gts.core.service.TxCompensationAction;
import com.wf.gts.core.service.TxCompensationService;

@Service
public class TxCompensationCommand implements Command {

    private final TxCompensationService txCompensationService;

    @Autowired
    public TxCompensationCommand(TxCompensationService txCompensationService) {
        this.txCompensationService = txCompensationService;
    }

    /**
     * 执行命令接口
     * @param txCompensationAction 封装命令信息
     */
    @Override
    public void execute(TxCompensationAction txCompensationAction) {
        txCompensationService.submit(txCompensationAction);
    }

    public String saveTxCompensation(TransactionInvocation invocation, String groupId, String taskId) {
        TxCompensationAction action = new TxCompensationAction();
        action.setCompensationActionEnum(CompensationActionEnum.SAVE);
        TransactionRecover recover = new TransactionRecover();
        recover.setRetriedCount(1);
        recover.setStatus(TransactionStatusEnum.BEGIN.getCode());
        recover.setId(IdWorkerUtils.getInstance().createGroupId());
        recover.setTransactionInvocation(invocation);
        recover.setGroupId(groupId);
        recover.setTaskId(taskId);
        recover.setCreateTime(new Date());
        action.setTransactionRecover(recover);
        execute(action);
        return recover.getId();
    }

    public void removeTxCompensation(String compensateId) {
        TxCompensationAction action = new TxCompensationAction();
        action.setCompensationActionEnum(CompensationActionEnum.DELETE);
        TransactionRecover recover = new TransactionRecover();
        recover.setId(compensateId);
        action.setTransactionRecover(recover);
        execute(action);
    }

}
