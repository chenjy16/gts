package com.wf.gts.remoting.protocol;
import com.wf.gts.remoting.protocol.DataVersion;
import io.netty.channel.Channel;

public  class BrokerLiveInfo {
  private long brokerId;
  private long lastUpdateTimestamp;
  private DataVersion dataVersion;
  private Channel channel;
  private String haServerAddr;
  private String brokerName;
  private String brokerAddr;

 

  public BrokerLiveInfo(long brokerId, long lastUpdateTimestamp, DataVersion dataVersion, Channel channel,
      String haServerAddr, String brokerName, String brokerAddr) {
    this.brokerId = brokerId;
    this.lastUpdateTimestamp = lastUpdateTimestamp;
    this.dataVersion = dataVersion;
    this.channel = channel;
    this.haServerAddr = haServerAddr;
    this.brokerName = brokerName;
    this.brokerAddr = brokerAddr;
  }

  public long getLastUpdateTimestamp() {
      return lastUpdateTimestamp;
  }

  public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
      this.lastUpdateTimestamp = lastUpdateTimestamp;
  }

  public DataVersion getDataVersion() {
      return dataVersion;
  }

  public void setDataVersion(DataVersion dataVersion) {
      this.dataVersion = dataVersion;
  }

  public Channel getChannel() {
      return channel;
  }

  public void setChannel(Channel channel) {
      this.channel = channel;
  }

  public String getHaServerAddr() {
      return haServerAddr;
  }

  public void setHaServerAddr(String haServerAddr) {
      this.haServerAddr = haServerAddr;
  }

  public long getBrokerId() {
    return brokerId;
  }

  public void setBrokerId(long brokerId) {
    this.brokerId = brokerId;
  }

  public String getBrokerName() {
    return brokerName;
  }

  public void setBrokerName(String brokerName) {
    this.brokerName = brokerName;
  }

  public String getBrokerAddr() {
    return brokerAddr;
  }

  public void setBrokerAddr(String brokerAddr) {
    this.brokerAddr = brokerAddr;
  }


}
