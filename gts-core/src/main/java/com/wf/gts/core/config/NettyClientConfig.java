package com.wf.gts.core.config;

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

  public boolean isClientCloseSocketIfTimeout() {
      return clientCloseSocketIfTimeout;
  }

  public void setClientCloseSocketIfTimeout(final boolean clientCloseSocketIfTimeout) {
      this.clientCloseSocketIfTimeout = clientCloseSocketIfTimeout;
  }

  public int getClientWorkerThreads() {
      return clientWorkerThreads;
  }

  public void setClientWorkerThreads(int clientWorkerThreads) {
      this.clientWorkerThreads = clientWorkerThreads;
  }


  public int getConnectTimeoutMillis() {
      return connectTimeoutMillis;
  }

  public void setConnectTimeoutMillis(int connectTimeoutMillis) {
      this.connectTimeoutMillis = connectTimeoutMillis;
  }

  public int getClientCallbackExecutorThreads() {
      return clientCallbackExecutorThreads;
  }

  public void setClientCallbackExecutorThreads(int clientCallbackExecutorThreads) {
      this.clientCallbackExecutorThreads = clientCallbackExecutorThreads;
  }

  public long getChannelNotActiveInterval() {
      return channelNotActiveInterval;
  }

  public void setChannelNotActiveInterval(long channelNotActiveInterval) {
      this.channelNotActiveInterval = channelNotActiveInterval;
  }



  public int getClientChannelMaxIdleTimeSeconds() {
      return clientChannelMaxIdleTimeSeconds;
  }

  public void setClientChannelMaxIdleTimeSeconds(int clientChannelMaxIdleTimeSeconds) {
      this.clientChannelMaxIdleTimeSeconds = clientChannelMaxIdleTimeSeconds;
  }


  public boolean isClientPooledByteBufAllocatorEnable() {
      return clientPooledByteBufAllocatorEnable;
  }

  public void setClientPooledByteBufAllocatorEnable(boolean clientPooledByteBufAllocatorEnable) {
      this.clientPooledByteBufAllocatorEnable = clientPooledByteBufAllocatorEnable;
  }

  public boolean isUseTLS() {
      return useTLS;
  }

  public void setUseTLS(boolean useTLS) {
      this.useTLS = useTLS;
  }

}
