package com.wf.gts.manage.config;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wf.gts.remoting.netty.NettyServerConfig;

@Configuration
@ConfigurationProperties(prefix = "gts.netty.server")
public class GtsManageNettyServerConfig {
  
  private int listenPort;
  private int serverWorkerThreads;
  private int serverCallbackExecutorThreads;
  private int serverSelectorThreads;
  private int serverOnewaySemaphoreValue;
  private int serverAsyncSemaphoreValue ;
  private int serverChannelMaxIdleTimeSeconds;
  private int serverSocketSndBufSize;
  private int serverSocketRcvBufSize;
  private boolean serverPooledByteBufAllocatorEnable;
  private boolean useEpollNativeSelector;
  
  
  @Bean
  public NettyServerConfig getNettyServerConfig() {
      NettyServerConfig cfg=new NettyServerConfig();
      cfg.setListenPort(listenPort);
      cfg.setServerAsyncSemaphoreValue(serverAsyncSemaphoreValue);
      cfg.setServerCallbackExecutorThreads(serverCallbackExecutorThreads);
      cfg.setServerChannelMaxIdleTimeSeconds(serverChannelMaxIdleTimeSeconds);
      cfg.setServerOnewaySemaphoreValue(serverOnewaySemaphoreValue);
      cfg.setServerPooledByteBufAllocatorEnable(serverPooledByteBufAllocatorEnable);
      cfg.setServerSelectorThreads(serverSelectorThreads);
      cfg.setServerSocketSndBufSize(serverSocketSndBufSize);
      cfg.setServerSocketRcvBufSize(serverSocketRcvBufSize);
      cfg.setServerWorkerThreads(serverWorkerThreads);
      cfg.setUseEpollNativeSelector(useEpollNativeSelector);
    return cfg;
  }


  public int getListenPort() {
    return listenPort;
  }


  public void setListenPort(int listenPort) {
    this.listenPort = listenPort;
  }


  public int getServerWorkerThreads() {
    return serverWorkerThreads;
  }


  public void setServerWorkerThreads(int serverWorkerThreads) {
    this.serverWorkerThreads = serverWorkerThreads;
  }


  public int getServerCallbackExecutorThreads() {
    return serverCallbackExecutorThreads;
  }


  public void setServerCallbackExecutorThreads(int serverCallbackExecutorThreads) {
    this.serverCallbackExecutorThreads = serverCallbackExecutorThreads;
  }


  public int getServerSelectorThreads() {
    return serverSelectorThreads;
  }


  public void setServerSelectorThreads(int serverSelectorThreads) {
    this.serverSelectorThreads = serverSelectorThreads;
  }


  public int getServerOnewaySemaphoreValue() {
    return serverOnewaySemaphoreValue;
  }


  public void setServerOnewaySemaphoreValue(int serverOnewaySemaphoreValue) {
    this.serverOnewaySemaphoreValue = serverOnewaySemaphoreValue;
  }


  public int getServerAsyncSemaphoreValue() {
    return serverAsyncSemaphoreValue;
  }


  public void setServerAsyncSemaphoreValue(int serverAsyncSemaphoreValue) {
    this.serverAsyncSemaphoreValue = serverAsyncSemaphoreValue;
  }


  public int getServerChannelMaxIdleTimeSeconds() {
    return serverChannelMaxIdleTimeSeconds;
  }


  public void setServerChannelMaxIdleTimeSeconds(int serverChannelMaxIdleTimeSeconds) {
    this.serverChannelMaxIdleTimeSeconds = serverChannelMaxIdleTimeSeconds;
  }


  public int getServerSocketSndBufSize() {
    return serverSocketSndBufSize;
  }


  public void setServerSocketSndBufSize(int serverSocketSndBufSize) {
    this.serverSocketSndBufSize = serverSocketSndBufSize;
  }


  public int getServerSocketRcvBufSize() {
    return serverSocketRcvBufSize;
  }


  public void setServerSocketRcvBufSize(int serverSocketRcvBufSize) {
    this.serverSocketRcvBufSize = serverSocketRcvBufSize;
  }


  public boolean isServerPooledByteBufAllocatorEnable() {
    return serverPooledByteBufAllocatorEnable;
  }


  public void setServerPooledByteBufAllocatorEnable(boolean serverPooledByteBufAllocatorEnable) {
    this.serverPooledByteBufAllocatorEnable = serverPooledByteBufAllocatorEnable;
  }


  public boolean isUseEpollNativeSelector() {
    return useEpollNativeSelector;
  }


  public void setUseEpollNativeSelector(boolean useEpollNativeSelector) {
    this.useEpollNativeSelector = useEpollNativeSelector;
  }
  
  
  
}
