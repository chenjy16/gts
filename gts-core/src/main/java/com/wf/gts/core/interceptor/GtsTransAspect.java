package com.wf.gts.core.interceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public abstract class GtsTransAspect {

    private GtsTransInterceptor gtsTransInterceptor;


    public void setGtsTransInterceptor(GtsTransInterceptor gtsTransInterceptor) {
      this.gtsTransInterceptor = gtsTransInterceptor;
    }

    @Pointcut("@annotation(com.wf.gts.core.annotation.GtsTransaction)")
    public void gtsTransInterceptor() {

    }

    @Around("gtsTransInterceptor()")
    public Object interceptCompensableMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return gtsTransInterceptor.interceptor(proceedingJoinPoint);
    }

    public abstract int getOrder();
}
