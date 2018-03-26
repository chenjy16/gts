package com.wf.gts.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Tx分布式事务拦截器接口
 */
@FunctionalInterface
public interface GtsTransInterceptor {

    Object interceptor(ProceedingJoinPoint pjp) throws Throwable;
}
