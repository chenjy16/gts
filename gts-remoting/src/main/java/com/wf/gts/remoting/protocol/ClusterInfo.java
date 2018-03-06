package com.wf.gts.remoting.protocol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ClusterInfo extends RemotingSerializable {
  
  private HashMap<String/* brokerName */, BrokerData> brokerAddrTable;
  
  private HashMap<String/* clusterName */, Set<String/* brokerName */>> clusterAddrTable;

  public HashMap<String, BrokerData> getBrokerAddrTable() {
      return brokerAddrTable;
  }

  public void setBrokerAddrTable(HashMap<String, BrokerData> brokerAddrTable) {
      this.brokerAddrTable = brokerAddrTable;
  }

  public HashMap<String, Set<String>> getClusterAddrTable() {
      return clusterAddrTable;
  }

  public void setClusterAddrTable(HashMap<String, Set<String>> clusterAddrTable) {
      this.clusterAddrTable = clusterAddrTable;
  }

  public String[] retrieveAllAddrByCluster(String cluster) {
      List<String> addrs = new ArrayList<String>();
      if (clusterAddrTable.containsKey(cluster)) {
          Set<String> brokerNames = clusterAddrTable.get(cluster);
          for (String brokerName : brokerNames) {
              BrokerData brokerData = brokerAddrTable.get(brokerName);
              if (null != brokerData) {
                  addrs.addAll(brokerData.getBrokerAddrs().values());
              }
          }
      }

      return addrs.toArray(new String[] {});
  }

  public String[] retrieveAllClusterNames() {
      return clusterAddrTable.keySet().toArray(new String[] {});
  }
  
}
