package com.wf.gts.core.service.impl;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wf.gts.core.annotation.TxTransaction;
import com.wf.gts.core.bean.TransactionInvocation;
import com.wf.gts.core.bean.TxTransactionInfo;
import com.wf.gts.core.handler.TxTransactionHandler;
import com.wf.gts.core.service.AspectTransactionService;
import com.wf.gts.core.service.TxTransactionFactoryService;
import com.wf.gts.core.util.SpringBeanUtils;

@Service
public class AspectTransactionServiceImpl implements AspectTransactionService {

    private final TxTransactionFactoryService txTransactionFactoryService;

    @Autowired
    public AspectTransactionServiceImpl(TxTransactionFactoryService txTransactionFactoryService) {
        this.txTransactionFactoryService = txTransactionFactoryService;
    }

    @Override
    public Object invoke(String transactionGroupId, ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        Object[] args = point.getArgs();
        Method thisMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
        TransactionInvocation invocation = new TransactionInvocation(clazz, thisMethod.getName(), args, method.getParameterTypes());
        TxTransaction  txTransaction=getTxTransaction(thisMethod);
        TxTransactionInfo info = new TxTransactionInfo(txTransaction, transactionGroupId, invocation);
        final Class c = txTransactionFactoryService.factoryOf(info);
        final TxTransactionHandler txTransactionHandler =
                (TxTransactionHandler) SpringBeanUtils.getInstance().getBean(c);

        return txTransactionHandler.handler(point, info);
    }
    
    
    
    private TxTransaction getTxTransaction(Method method){
      //获取方法上的注解
      TxTransaction  txTransaction = method.getAnnotation(TxTransaction.class);
      return txTransaction;
    }
}
