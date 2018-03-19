package com.wf.gts.nameserver;
import java.io.File;

import com.wf.gts.remoting.core.MixAll;


public class NamesrvConfig {
  
  private String rocketmqHome = System.getProperty(MixAll.ROCKETMQ_HOME_PROPERTY, System.getenv(MixAll.ROCKETMQ_HOME_ENV));  
  private String kvConfigPath = System.getProperty("user.home") + File.separator + "namesrv" + File.separator + "kvConfig.json";
  private String configStorePath = System.getProperty("user.home") + File.separator + "namesrv" + File.separator + "namesrv.properties";
  
  private boolean clusterTest = false;
  private boolean orderMessageEnable = false;

  
  public boolean isOrderMessageEnable() {
      return orderMessageEnable;
  }

  public void setOrderMessageEnable(boolean orderMessageEnable) {
      this.orderMessageEnable = orderMessageEnable;
  }

  public String getRocketmqHome() {
      return rocketmqHome;
  }

  public void setRocketmqHome(String rocketmqHome) {
      this.rocketmqHome = rocketmqHome;
  }

  public String getKvConfigPath() {
      return kvConfigPath;
  }

  public void setKvConfigPath(String kvConfigPath) {
      this.kvConfigPath = kvConfigPath;
  }



  public boolean isClusterTest() {
      return clusterTest;
  }

  public void setClusterTest(boolean clusterTest) {
      this.clusterTest = clusterTest;
  }

  public String getConfigStorePath() {
      return configStorePath;
  }

  public void setConfigStorePath(final String configStorePath) {
      this.configStorePath = configStorePath;
  }

}
