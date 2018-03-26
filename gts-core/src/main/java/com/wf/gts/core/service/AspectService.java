package com.wf.gts.core.service;

import org.aspectj.lang.ProceedingJoinPoint;


public interface AspectService {

    Object invoke(String transactionGroupId, ProceedingJoinPoint point) throws Throwable;
}
