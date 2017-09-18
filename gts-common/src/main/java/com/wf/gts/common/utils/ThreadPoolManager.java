package com.wf.gts.common.utils;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPoolManager {

  private static Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);
  
  private ThreadPoolExecutor threadPool;
  
  private ArrayBlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(10000);
  
  /** 核心线程个数 **/
  private static int corePoolSize = Runtime.getRuntime().availableProcessors() + 2;
  /** 最大线程个数 **/
  private static int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 2;
  /** 保持心跳时间 **/
  private static int keepAliveTime = 1;
  /** 定时执行线程个数 **/
  private final static int minSchedule = 2;
  /** 线程池实例化 **/
  private static ThreadPoolManager threadPoolManage = new ThreadPoolManager();
  /** 延时执行线程 **/
  private ScheduledExecutorService appSchedule;

  private ThreadPoolManager() {
    RejectedExecutionHandler myHandler = new RejectedExecutionHandler() {
      public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        taskQueue.offer(r);
      }
    };
    Runnable command = new Runnable() {
      public void run() {
        Runnable task = null;
        try {
          task = taskQueue.take();// 使用具备阻塞特性的方法
        } catch (InterruptedException e) {
          return;
        }
        threadPool.execute(task);
      }
    };
    
    ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);
    // 每一次执行终止和下一次执行开始之间都存在给定的延迟 35毫秒
    scheduledPool.scheduleWithFixedDelay(command, 0L, 35L, TimeUnit.MILLISECONDS);
    threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
        new ArrayBlockingQueue<Runnable>(20), new NamedThreadFactory("CoreImplServiceHandler"), myHandler) {
      public void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        printException(r, t);
      }
    };
    appSchedule = Executors.newScheduledThreadPool(minSchedule);
  }

  private static void printException(Runnable r, Throwable t) {
      if (t == null && r instanceof Future<?>) {
        try {
          Future<?> future = (Future<?>) r;
          if (future.isDone())
            future.get();
        } catch (CancellationException ce) {
          t = ce;
        } catch (ExecutionException ee) {
          t = ee.getCause();
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
        }
      }
      if (t != null) {
        logger.error("系统自有线程池任务异常,error_msg==" + t.getMessage(), t);
      }
  }

  /**
   * 功能描述: 得到线程池的实例
   * 
   * @author: chenjy
   * @date: 2017年5月25日 上午11:08:32
   * @return
   */
  public static ThreadPoolManager getInstance() {
    return threadPoolManage;
  }

  public int getActiveCount() {
    return threadPool.getActiveCount();
  }

  public int getCorePoolCount() {
    return corePoolSize;
  }

  /**
   * 功能描述: 任务添加到线程池中
   * 
   * @author: chenjy
   * @date: 2017年5月25日 上午11:08:47
   * @param paramRunnable
   * @return
   */
  public Future<?> addExecuteTask(Runnable paramRunnable) {
    if (paramRunnable == null)
      return null;
    return this.threadPool.submit(paramRunnable);
  }

  /**
   * 功能描述: 延时任务添加到线程池中
   * 
   * @author: chenjy
   * @date: 2017年5月25日 上午11:09:04
   * @param task
   * @param delayTime
   */
  public void addDelayExecuteTask(Runnable task, int delayTime) {
    appSchedule.schedule(new DelayTask(task), delayTime, TimeUnit.MILLISECONDS);
  }

  /**
   * 功能描述: 延时固定周期执行
   * 
   * @author: chenjy
   * @date: 2017年5月25日 上午11:09:17
   * @param task
   * @param delay
   * @param period
   */
  public void addPeriodDelayExecuteTask(Runnable task, Long delay, Long period) {
    this.appSchedule.scheduleWithFixedDelay(new DelayTask(task), delay, period, TimeUnit.MILLISECONDS);
  }

  /** 延时任务 **/
  class DelayTask implements Runnable {
      private Runnable task;
      public DelayTask(Runnable paramTask) {
        this.task = paramTask;
      }
      public void run() {
        ThreadPoolManager.getInstance().addExecuteTask(task);
      }
  }

}
