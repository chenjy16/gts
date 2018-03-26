package com.wf.gts.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式事务注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GtsTransaction {
  
  //调用方事务超时时间
  long clientTransTimeout() default 3000L;
  
  //被调用方事务超时时间
  long serviceTransTimeout() default 3000L;
  
  //被调用方事务超时时间
  long socketTimeout() default 3000L;
  
  //是否通知
  boolean isNotice() default false;
}
