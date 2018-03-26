package com.wf.gts.core.service.impl;
import java.lang.reflect.Method;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wf.gts.common.beans.TransactionInvocation;
import com.wf.gts.core.annotation.GtsTransaction;
import com.wf.gts.core.bean.GtsTransInfo;
import com.wf.gts.core.handler.GtsTransHandler;
import com.wf.gts.core.service.AspectService;
import com.wf.gts.core.service.GtsFactoryService;
import com.wf.gts.core.util.SpringBeanUtils;

@Service
public class AspectServiceImpl implements AspectService {

    private final GtsFactoryService gtsFactoryService;

    @Autowired
    public AspectServiceImpl(GtsFactoryService gtsFactoryService) {
        this.gtsFactoryService = gtsFactoryService;
    }

    @Override
    public Object invoke(String transactionGroupId, ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        Object[] args = point.getArgs();
        Method thisMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
        
        TransactionInvocation invocation = new TransactionInvocation(clazz, thisMethod.getName(), args, method.getParameterTypes());
        GtsTransaction  txTransaction=getTxTransaction(thisMethod);
        GtsTransInfo info = new GtsTransInfo(txTransaction, transactionGroupId, invocation);
        Class c = gtsFactoryService.factoryOf(info);
        
        GtsTransHandler gtsTransHandler =(GtsTransHandler) SpringBeanUtils.getInstance().getBean(c);
        
        return gtsTransHandler.handler(point, info);
    }
    
    /**
     * 功能描述: 获取方法上的注解
     * @author: chenjy
     * @date: 2017年9月18日 下午2:48:05 
     * @param method
     * @return
     */
    private GtsTransaction getTxTransaction(Method method){
      GtsTransaction  gtsTransaction = method.getAnnotation(GtsTransaction.class);
      return gtsTransaction;
    }
}
