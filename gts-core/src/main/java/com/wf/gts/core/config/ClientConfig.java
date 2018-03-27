package com.wf.gts.core.config;
import com.wf.gts.remoting.core.RemotingUtil;
import com.wf.gts.remoting.netty.NettyClientConfig;
import com.wf.gts.remoting.netty.TlsSystemConfig;

import lombok.Getter;
import lombok.Setter;

/**
 * 事务基本信息配置类
 */
@Getter
@Setter
public class ClientConfig {

  private int clientCallbackExecutorThreads = Runtime.getRuntime().availableProcessors();

  private String namesrvAddr;
  private String clientIP = RemotingUtil.getLocalAddress();
  private String instanceName ="DEFAULT";
  private int pollNameServerInterval = 1000 * 30;
  private int heartbeatBrokerInterval = 1000 * 30;
  private long timeoutMillis=3000L;
  
  
  
  private boolean useTLS = TlsSystemConfig.tlsEnable;
  private int clientWorkerThreads = 4;
  private int clientOnewaySemaphoreValue = 65535;
  private int clientAsyncSemaphoreValue = 65535;
  private int connectTimeoutMillis = 3000;
  private long channelNotActiveInterval = 1000 * 60;
  private int clientChannelMaxIdleTimeSeconds = 120;
  private int clientSocketSndBufSize = 65535; //64K
  private int clientSocketRcvBufSize = 65535; //64K
  private boolean clientPooledByteBufAllocatorEnable = false;
  private boolean clientCloseSocketIfTimeout = false;
  
  
  
  public String buildMQClientId() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.getClientIP());
      sb.append("@");
      sb.append(this.getInstanceName());
      return sb.toString();
  }
  
  public ClientConfig cloneClientConfig() {
    ClientConfig cc = new ClientConfig();
    cc.namesrvAddr = namesrvAddr;
    cc.clientIP = clientIP;
    cc.clientCallbackExecutorThreads = clientCallbackExecutorThreads;
    cc.useTLS = useTLS;
    return cc;
  }
  
  public NettyClientConfig buildNettyClientConfig() {
    NettyClientConfig nettyClientConfig = new NettyClientConfig();
    nettyClientConfig.setClientCallbackExecutorThreads(clientCallbackExecutorThreads);
    nettyClientConfig.setUseTLS(useTLS);
    nettyClientConfig.setClientOnewaySemaphoreValue(clientOnewaySemaphoreValue);
    nettyClientConfig.setClientAsyncSemaphoreValue(clientAsyncSemaphoreValue);
    nettyClientConfig.setConnectTimeoutMillis(connectTimeoutMillis);
    nettyClientConfig.setChannelNotActiveInterval(channelNotActiveInterval);
    nettyClientConfig.setClientChannelMaxIdleTimeSeconds(clientChannelMaxIdleTimeSeconds);
    nettyClientConfig.setClientWorkerThreads(clientWorkerThreads);
    nettyClientConfig.setClientSocketSndBufSize(clientSocketSndBufSize);
    nettyClientConfig.setClientSocketRcvBufSize(clientSocketRcvBufSize);
    nettyClientConfig.setClientPooledByteBufAllocatorEnable(clientPooledByteBufAllocatorEnable);
    return nettyClientConfig;
  }
}
