package com.wf.gts.nameserver.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wf.gts.remoting.netty.NettyServerConfig;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "gts.netty.server")
public class nameServNettyServerConfig {
  
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


  
}
