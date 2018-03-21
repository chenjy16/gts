package com.wf.gts.core.config;
import com.wf.gts.remoting.core.RemotingUtil;
import com.wf.gts.remoting.netty.TlsSystemConfig;

/**
 * TxTransaction 事务基本信息配置类
 */
public class ClientConfig {

  private int clientCallbackExecutorThreads = Runtime.getRuntime().availableProcessors();
  private boolean useTLS = TlsSystemConfig.tlsEnable;
  private String namesrvAddr;//
  private String clientIP = RemotingUtil.getLocalAddress();
  private String instanceName ="DEFAULT";
  private int pollNameServerInterval = 1000 * 30;
  private int heartbeatBrokerInterval = 1000 * 30;
  private long timeoutMillis=3000L;
  
  
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
  
  public int getClientCallbackExecutorThreads() {
    return clientCallbackExecutorThreads;
  }
  
  public void setClientCallbackExecutorThreads(int clientCallbackExecutorThreads) {
    this.clientCallbackExecutorThreads = clientCallbackExecutorThreads;
  }
  
  public boolean isUseTLS() {
    return useTLS;
  }
  public void setUseTLS(boolean useTLS) {
    this.useTLS = useTLS;
  }
  public String getNamesrvAddr() {
    return namesrvAddr;
  }
  public void setNamesrvAddr(String namesrvAddr) {
    this.namesrvAddr = namesrvAddr;
  }

  public String getClientIP() {
    return clientIP;
  }

  public void setClientIP(String clientIP) {
    this.clientIP = clientIP;
  }


  public String getInstanceName() {
    return instanceName;
  }



  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }



  public int getPollNameServerInterval() {
    return pollNameServerInterval;
  }



  public void setPollNameServerInterval(int pollNameServerInterval) {
    this.pollNameServerInterval = pollNameServerInterval;
  }



  public int getHeartbeatBrokerInterval() {
    return heartbeatBrokerInterval;
  }



  public void setHeartbeatBrokerInterval(int heartbeatBrokerInterval) {
    this.heartbeatBrokerInterval = heartbeatBrokerInterval;
  }



  public long getTimeoutMillis() {
    return timeoutMillis;
  }


  public void setTimeoutMillis(long timeoutMillis) {
    this.timeoutMillis = timeoutMillis;
  }
  
    
}
