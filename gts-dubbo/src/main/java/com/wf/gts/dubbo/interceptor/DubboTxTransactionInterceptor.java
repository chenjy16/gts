package com.wf.gts.dubbo.interceptor;
import org.aspectj.lang.ProceedingJoinPoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.rpc.RpcContext;
import com.wf.gts.core.interceptor.TxTransactionInterceptor;
import com.wf.gts.core.service.AspectTransactionService;


@Component
public class DubboTxTransactionInterceptor implements TxTransactionInterceptor {

    private final AspectTransactionService aspectTransactionService;

    @Autowired
    public DubboTxTransactionInterceptor(AspectTransactionService aspectTransactionService) {
        this.aspectTransactionService = aspectTransactionService;
    }

    @Override
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        String groupId = RpcContext.getContext().getAttachment("tx-group");
        return aspectTransactionService.invoke(groupId,pjp);
    }

}
