package com.wf.gts.manage.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.wf.gts.remoting.netty.NettyClientConfig;


@Configuration
@ConfigurationProperties(prefix = "gts.netty.client")
public class GtsManageNettyClientConfig {
  
  private int     clientWorkerThreads;
  private int     clientCallbackExecutorThreads;
  private int     clientOnewaySemaphoreValue;
  private int     clientAsyncSemaphoreValue;
  private int     connectTimeoutMillis;
  private long    channelNotActiveInterval;
  private int     clientChannelMaxIdleTimeSeconds;
  private int     clientSocketSndBufSize;
  private int     clientSocketRcvBufSize;
  private boolean clientPooledByteBufAllocatorEnable;
  private boolean clientCloseSocketIfTimeout;
  private boolean useTLS;
  
  
  
  @Bean
  public NettyClientConfig getNettyClientConfig(){
    NettyClientConfig cfg=new NettyClientConfig();
    cfg.setChannelNotActiveInterval(channelNotActiveInterval);
    cfg.setClientAsyncSemaphoreValue(clientAsyncSemaphoreValue);
    cfg.setClientCallbackExecutorThreads(clientCallbackExecutorThreads);
    cfg.setClientCloseSocketIfTimeout(clientCloseSocketIfTimeout);
    cfg.setClientOnewaySemaphoreValue(clientOnewaySemaphoreValue);
    cfg.setClientPooledByteBufAllocatorEnable(clientPooledByteBufAllocatorEnable);
    cfg.setClientSocketRcvBufSize(clientSocketRcvBufSize);
    cfg.setClientSocketSndBufSize(clientSocketSndBufSize);
    cfg.setClientWorkerThreads(clientWorkerThreads);
    cfg.setUseTLS(useTLS);
    cfg.setConnectTimeoutMillis(connectTimeoutMillis);
    cfg.setClientChannelMaxIdleTimeSeconds(clientChannelMaxIdleTimeSeconds);
    return cfg;
    
  }



  public int getClientWorkerThreads() {
    return clientWorkerThreads;
  }



  public void setClientWorkerThreads(int clientWorkerThreads) {
    this.clientWorkerThreads = clientWorkerThreads;
  }



  public int getClientCallbackExecutorThreads() {
    return clientCallbackExecutorThreads;
  }



  public void setClientCallbackExecutorThreads(int clientCallbackExecutorThreads) {
    this.clientCallbackExecutorThreads = clientCallbackExecutorThreads;
  }



  public int getClientOnewaySemaphoreValue() {
    return clientOnewaySemaphoreValue;
  }



  public void setClientOnewaySemaphoreValue(int clientOnewaySemaphoreValue) {
    this.clientOnewaySemaphoreValue = clientOnewaySemaphoreValue;
  }



  public int getClientAsyncSemaphoreValue() {
    return clientAsyncSemaphoreValue;
  }



  public void setClientAsyncSemaphoreValue(int clientAsyncSemaphoreValue) {
    this.clientAsyncSemaphoreValue = clientAsyncSemaphoreValue;
  }



  public int getConnectTimeoutMillis() {
    return connectTimeoutMillis;
  }



  public void setConnectTimeoutMillis(int connectTimeoutMillis) {
    this.connectTimeoutMillis = connectTimeoutMillis;
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



  public int getClientSocketSndBufSize() {
    return clientSocketSndBufSize;
  }



  public void setClientSocketSndBufSize(int clientSocketSndBufSize) {
    this.clientSocketSndBufSize = clientSocketSndBufSize;
  }



  public int getClientSocketRcvBufSize() {
    return clientSocketRcvBufSize;
  }



  public void setClientSocketRcvBufSize(int clientSocketRcvBufSize) {
    this.clientSocketRcvBufSize = clientSocketRcvBufSize;
  }



  public boolean isClientPooledByteBufAllocatorEnable() {
    return clientPooledByteBufAllocatorEnable;
  }



  public void setClientPooledByteBufAllocatorEnable(boolean clientPooledByteBufAllocatorEnable) {
    this.clientPooledByteBufAllocatorEnable = clientPooledByteBufAllocatorEnable;
  }



  public boolean isClientCloseSocketIfTimeout() {
    return clientCloseSocketIfTimeout;
  }



  public void setClientCloseSocketIfTimeout(boolean clientCloseSocketIfTimeout) {
    this.clientCloseSocketIfTimeout = clientCloseSocketIfTimeout;
  }



  public boolean isUseTLS() {
    return useTLS;
  }



  public void setUseTLS(boolean useTLS) {
    this.useTLS = useTLS;
  }
  
  
  
  

}
