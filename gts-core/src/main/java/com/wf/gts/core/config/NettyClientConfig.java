package com.wf.gts.core.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NettyClientConfig {
  
  /**
   * Worker thread number
   */
  private int clientWorkerThreads = 4;
  private int clientCallbackExecutorThreads = Runtime.getRuntime().availableProcessors();
  private int connectTimeoutMillis = 3000;
  private long channelNotActiveInterval = 1000 * 60;

  /**
   * IdleStateEvent will be triggered when neither read nor write was performed for
   * the specified period of this time. Specify {@code 0} to disable
   */
  private int clientChannelMaxIdleTimeSeconds = 120;

  private boolean clientPooledByteBufAllocatorEnable = false;
  private boolean clientCloseSocketIfTimeout = false;
  private boolean useTLS;

 
}
