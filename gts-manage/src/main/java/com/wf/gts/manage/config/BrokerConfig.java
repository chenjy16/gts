package com.wf.gts.manage.config;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.wf.gts.remoting.util.MixAll;
import com.wf.gts.remoting.util.PermName;
import com.wf.gts.remoting.util.RemotingUtil;

public class BrokerConfig {
    private String brokerIP1 = RemotingUtil.getLocalAddress();
    private String brokerIP2 = RemotingUtil.getLocalAddress();
    private String namesrvAddr = "localhost:9876";
    private int  listenPort;
    private int  haListenPort;
    private String brokerName = localHostName();
    private String brokerClusterName = "DefaultCluster";
    private long brokerId = MixAll.MASTER_ID;
    private int brokerPermission = PermName.PERM_READ | PermName.PERM_WRITE;

    private int registerBrokerTimeoutMills = 6000;
    
    public static String localHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }
        return "DEFAULT_BROKER";
    }

    public String getBrokerIP1() {
      return brokerIP1;
    }

    public void setBrokerIP1(String brokerIP1) {
      this.brokerIP1 = brokerIP1;
    }

    public String getBrokerIP2() {
      return brokerIP2;
    }

    public void setBrokerIP2(String brokerIP2) {
      this.brokerIP2 = brokerIP2;
    }

    public String getBrokerName() {
      return brokerName;
    }

    public void setBrokerName(String brokerName) {
      this.brokerName = brokerName;
    }

    public String getBrokerClusterName() {
      return brokerClusterName;
    }

    public void setBrokerClusterName(String brokerClusterName) {
      this.brokerClusterName = brokerClusterName;
    }

    public long getBrokerId() {
      return brokerId;
    }

    public void setBrokerId(long brokerId) {
      this.brokerId = brokerId;
    }

    public int getBrokerPermission() {
      return brokerPermission;
    }

    public void setBrokerPermission(int brokerPermission) {
      this.brokerPermission = brokerPermission;
    }

    public int getRegisterBrokerTimeoutMills() {
      return registerBrokerTimeoutMills;
    }

    public void setRegisterBrokerTimeoutMills(int registerBrokerTimeoutMills) {
      this.registerBrokerTimeoutMills = registerBrokerTimeoutMills;
    }

    public int getListenPort() {
      return listenPort;
    }

    public void setListenPort(int listenPort) {
      this.listenPort = listenPort;
    }

    public int getHaListenPort() {
      return haListenPort;
    }

    public void setHaListenPort(int haListenPort) {
      this.haListenPort = haListenPort;
    }

    public String getNamesrvAddr() {
      return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
      this.namesrvAddr = namesrvAddr;
    }
   
    
    
}
