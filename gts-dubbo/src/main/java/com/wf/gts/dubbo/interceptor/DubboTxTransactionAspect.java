package com.wf.gts.dubbo.interceptor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import com.wf.gts.core.interceptor.TxTransactionAspect;


/**
 * DubboTxTransactionAspect 切面
 */
@Aspect
@Component
public class DubboTxTransactionAspect extends TxTransactionAspect implements Ordered {

    @Autowired
    public DubboTxTransactionAspect(DubboTxTransactionInterceptor dubboTxTransactionInterceptor) {
        this.setTxTransactionInterceptor(dubboTxTransactionInterceptor);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
