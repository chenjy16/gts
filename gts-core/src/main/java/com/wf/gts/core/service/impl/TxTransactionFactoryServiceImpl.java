package com.wf.gts.core.service.impl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.wf.gts.core.bean.TxTransactionInfo;
import com.wf.gts.core.handler.ActorTxTransactionHandler;
import com.wf.gts.core.handler.StartTxTransactionHandler;
import com.wf.gts.core.service.TxTransactionFactoryService;

/**
 * 判断是进行start 还是running 还是补偿
 */
@Service
public class TxTransactionFactoryServiceImpl implements TxTransactionFactoryService {

    @Override
    public Class factoryOf(TxTransactionInfo info) throws Throwable {
        if (StringUtils.isBlank(info.getTxGroupId())) {
            return StartTxTransactionHandler.class;
        } else {
            return ActorTxTransactionHandler.class;
        }

    }
}
