package com.wf.gts.core.interceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public abstract class TxTransactionAspect {

    private TxTransactionInterceptor txTransactionInterceptor;

    public void setTxTransactionInterceptor(TxTransactionInterceptor txTransactionInterceptor) {
        this.txTransactionInterceptor = txTransactionInterceptor;
    }

    @Pointcut("@annotation(com.wf.gts.core.annotation.TxTransaction)")
    public void txTransactionInterceptor() {

    }

    @Around("txTransactionInterceptor()")
    public Object interceptCompensableMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return txTransactionInterceptor.interceptor(proceedingJoinPoint);
}

    public abstract int getOrder();
}
