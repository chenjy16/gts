package com.wf.gts.manage.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wf.gts.remoting.netty.NettyClientConfig;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
  

}
