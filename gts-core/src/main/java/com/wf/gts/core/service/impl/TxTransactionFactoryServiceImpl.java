package com.wf.gts.core.service.impl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.wf.gts.core.bean.TxTransactionInfo;
import com.wf.gts.core.handler.JoinTxTransactionHandler;
import com.wf.gts.core.handler.StartTxTransactionHandler;
import com.wf.gts.core.service.TxTransactionFactoryService;


@Service
public class TxTransactionFactoryServiceImpl implements TxTransactionFactoryService<TxTransactionInfo> {
    
    @Override
    public Class factoryOf(TxTransactionInfo info) throws Throwable {
        if (StringUtils.isBlank(info.getTxGroupId())) {
            return StartTxTransactionHandler.class;
        } else {
            return JoinTxTransactionHandler.class;
        }

    }
}
