package com.wf.gts.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tx分布式事务注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TxTransaction {
  
  //调用方超时时间
  int clientWaitMaxTime() default 3000;
  
  //被调用方超时时间
  int serviceWaitMaxTime() default 3000;
  
  //是否通知
  boolean isNotice() default false;
}
