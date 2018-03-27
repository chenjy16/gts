package com.wf.gts.remoting.protocol;


public class GtsManageLiveAddr {
  
  private long gtsManageId;
  private long lastUpdateTimestamp;
  private String gtsManageName;
  private String gtsManageAddr;
  
  
  public GtsManageLiveAddr() {
    
  }
  
  
  public GtsManageLiveAddr(long gtsManageId, long lastUpdateTimestamp, String gtsManageName, String gtsManageAddr) {
    this.gtsManageId = gtsManageId;
    this.lastUpdateTimestamp = lastUpdateTimestamp;
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
