package com.wf.gts.manage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(ignoreUnknownFields = false,prefix = "gts.manage")
public class GtsManageConfig {
  
  private String namesrvAddr;
  private String manageName;
  private long manageId;
  private int registerBrokerTimeoutMills = 6000;
  private String manageAddr;
  private String haServerAddr;
  private int defaultThreadPoolNums;
  private int clientManageThreadPoolNums;
  private int clientManagerThreadPoolQueueCapacity;
  
  
  
  public String getNamesrvAddr() {
    return namesrvAddr;
  }
  public void setNamesrvAddr(String namesrvAddr) {
    this.namesrvAddr = namesrvAddr;
  }
  public String getManageName() {
    return manageName;
  }
  public void setManageName(String manageName) {
    this.manageName = manageName;
  }
  public long getManageId() {
    return manageId;
  }
  public void setManageId(long manageId) {
    this.manageId = manageId;
  }
  public int getRegisterBrokerTimeoutMills() {
    return registerBrokerTimeoutMills;
  }
  public void setRegisterBrokerTimeoutMills(int registerBrokerTimeoutMills) {
    this.registerBrokerTimeoutMills = registerBrokerTimeoutMills;
  }
  public String getManageAddr() {
    return manageAddr;
  }
  public void setManageAddr(String manageAddr) {
    this.manageAddr = manageAddr;
  }
  public String getHaServerAddr() {
    return haServerAddr;
  }
  public void setHaServerAddr(String haServerAddr) {
    this.haServerAddr = haServerAddr;
  }
  public int getDefaultThreadPoolNums() {
    return defaultThreadPoolNums;
  }
  public void setDefaultThreadPoolNums(int defaultThreadPoolNums) {
    this.defaultThreadPoolNums = defaultThreadPoolNums;
  }
  public int getClientManageThreadPoolNums() {
    return clientManageThreadPoolNums;
  }
  public void setClientManageThreadPoolNums(int clientManageThreadPoolNums) {
    this.clientManageThreadPoolNums = clientManageThreadPoolNums;
  }
  public int getClientManagerThreadPoolQueueCapacity() {
    return clientManagerThreadPoolQueueCapacity;
  }
  public void setClientManagerThreadPoolQueueCapacity(int clientManagerThreadPoolQueueCapacity) {
    this.clientManagerThreadPoolQueueCapacity = clientManagerThreadPoolQueueCapacity;
  }
}
