package com.wf.gts.core.config;
import com.wf.gts.remoting.netty.TlsSystemConfig;
import com.wf.gts.remoting.util.RemotingUtil;

public class ClientConfig extends TxConfig{
  
  private int clientCallbackExecutorThreads = Runtime.getRuntime().availableProcessors();
  private boolean useTLS = TlsSystemConfig.tlsEnable;
  private String namesrvAddr;
  private String clientIP = RemotingUtil.getLocalAddress();
  
  
  public String buildMQClientId() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClientIP());
    sb.append("@");
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
  
}
