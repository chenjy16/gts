package com.wf.gts.remoting.protocol;
import com.wf.gts.remoting.protocol.DataVersion;
import io.netty.channel.Channel;

public  class GtsManageLiveInfo {
  private long gtsManageId;
  private long lastUpdateTimestamp;
  private DataVersion dataVersion;
  private Channel channel;
  private String gtsManageName;
  private String gtsManageAddr;
  
  
  
  
  
  public GtsManageLiveInfo(long gtsManageId, long lastUpdateTimestamp, DataVersion dataVersion, Channel channel,
      String gtsManageName, String gtsManageAddr) {
    this.gtsManageId = gtsManageId;
    this.lastUpdateTimestamp = lastUpdateTimestamp;
    this.dataVersion = dataVersion;
    this.channel = channel;
    this.gtsManageName = gtsManageName;
    this.gtsManageAddr = gtsManageAddr;
  }
  
  
  public long getGtsManageId() {
    return gtsManageId;
  }
  public void setGtsManageId(long gtsManageId) {
    this.gtsManageId = gtsManageId;
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
  public String getGtsManageName() {
    return gtsManageName;
  }
  public void setGtsManageName(String gtsManageName) {
    this.gtsManageName = gtsManageName;
  }
  public String getGtsManageAddr() {
    return gtsManageAddr;
  }
  public void setGtsManageAddr(String gtsManageAddr) {
    this.gtsManageAddr = gtsManageAddr;
  }

 


}
