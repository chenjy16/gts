package com.wf.gts.dubbo.interceptor;
import org.aspectj.lang.ProceedingJoinPoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.rpc.RpcContext;
import com.wf.gts.core.interceptor.GtsTransInterceptor;
import com.wf.gts.core.service.AspectService;


@Component
public class DubboGtsTransInterceptor implements GtsTransInterceptor {

    private final AspectService aspectTransactionService;

    @Autowired
    public DubboGtsTransInterceptor(AspectService aspectTransactionService) {
        this.aspectTransactionService = aspectTransactionService;
    }

    @Override
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        String groupId = RpcContext.getContext().getAttachment("GtsTransGroupId");
        return aspectTransactionService.invoke(groupId,pjp);
    }

}
