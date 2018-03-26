package com.wf.gts.dubbo.interceptor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import com.wf.gts.core.interceptor.GtsTransAspect;


/**
 * DubboTxTransactionAspect 切面
 */
@Aspect
@Component
public class DubboGtsTransAspect extends GtsTransAspect implements Ordered {

    @Autowired
    public DubboGtsTransAspect(DubboGtsTransInterceptor dubboTxTransactionInterceptor) {
        this.setGtsTransInterceptor(dubboTxTransactionInterceptor);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
