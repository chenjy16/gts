package com.wf.gts.core.handler;
import org.aspectj.lang.ProceedingJoinPoint;
import com.wf.gts.core.bean.GtsTransInfo;


@FunctionalInterface
public interface GtsTransHandler {
  
  /**
   * 分布式事务处理接口
   * @param point point 切点
   * @param info  信息
   * @return Object
   * @throws Throwable
   */
  Object handler(ProceedingJoinPoint point, GtsTransInfo info) throws Throwable;

}
