package com.wf.gts.nameserver.route;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.remoting.protocol.BrokerData;
import com.wf.gts.remoting.protocol.ClusterInfo;
import com.wf.gts.remoting.protocol.DataVersion;
import com.wf.gts.remoting.protocol.QueueData;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.TopicConfig;
import com.wf.gts.remoting.protocol.TopicConfigSerializeWrapper;
import com.wf.gts.remoting.protocol.TopicRouteData;
import com.wf.gts.remoting.util.MixAll;
import com.wf.gts.remoting.util.RemotingUtil;
import io.netty.channel.Channel;


public class RouteInfoManager {
  
  private static final Logger log = LoggerFactory.getLogger(RouteInfoManager.class);
  private final static long BROKER_CHANNEL_EXPIRED_TIME = 1000 * 60 * 2;
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final HashMap<String/* topic */, List<QueueData>> topicQueueTable;
  private final HashMap<String/* brokerName */, BrokerData> brokerAddrTable;
  private final HashMap<String/* clusterName */, Set<String/* brokerName */>> clusterAddrTable;
  private final HashMap<String/* brokerAddr */, BrokerLiveInfo> brokerLiveTable;
  private final HashMap<String/* brokerAddr */, List<String>/* Filter Server */> filterServerTable;

  public RouteInfoManager() {
      this.topicQueueTable = new HashMap<String, List<QueueData>>(1024);
      this.brokerAddrTable = new HashMap<String, BrokerData>(128);
      this.clusterAddrTable = new HashMap<String, Set<String>>(32);
      this.brokerLiveTable = new HashMap<String, BrokerLiveInfo>(256);
      this.filterServerTable = new HashMap<String, List<String>>(256);
  }

  public byte[] getAllClusterInfo() {
      ClusterInfo clusterInfoSerializeWrapper = new ClusterInfo();
      clusterInfoSerializeWrapper.setBrokerAddrTable(this.brokerAddrTable);
      clusterInfoSerializeWrapper.setClusterAddrTable(this.clusterAddrTable);
      return clusterInfoSerializeWrapper.encode();
  }

  
  /**
   * 功能描述: 注册gts服务
   * @author: chenjy
   * @date: 2018年3月5日 下午3:11:44 
   * @param clusterName
   * @param brokerAddr
   * @param brokerName
   * @param brokerId
   * @param haServerAddr
   * @param topicConfigWrapper
   * @param filterServerList
   * @param channel
   * @return
   */
  public RegisterBrokerResult registerBroker(
      final String clusterName,
      final String brokerAddr,
      final String brokerName,
      final long brokerId,
      final String haServerAddr,
      final TopicConfigSerializeWrapper topicConfigWrapper,
      final List<String> filterServerList,
      final Channel channel) {
    
      RegisterBrokerResult result = new RegisterBrokerResult();
      try {
          try {
              this.lock.writeLock().lockInterruptibly();
              Set<String> brokerNames = this.clusterAddrTable.get(clusterName);
              if (null == brokerNames) {
                  brokerNames = new HashSet<String>();
                  this.clusterAddrTable.put(clusterName, brokerNames);
              }
              brokerNames.add(brokerName);
              boolean registerFirst = false;
              BrokerData brokerData = this.brokerAddrTable.get(brokerName);
              if (null == brokerData) {
                  registerFirst = true;
                  brokerData = new BrokerData(clusterName, brokerName, new HashMap<Long, String>());
                  this.brokerAddrTable.put(brokerName, brokerData);
              }
              String oldAddr = brokerData.getBrokerAddrs().put(brokerId, brokerAddr);
              registerFirst = registerFirst || (null == oldAddr);


              BrokerLiveInfo prevBrokerLiveInfo = this.brokerLiveTable.put(brokerAddr,
                  new BrokerLiveInfo(
                      System.currentTimeMillis(),
                      topicConfigWrapper.getDataVersion(),
                      channel,
                      haServerAddr));
              if (null == prevBrokerLiveInfo) {
                  log.info("new broker registered, {} HAServer: {}", brokerAddr, haServerAddr);
              }

              if (MixAll.MASTER_ID != brokerId) {
                  String masterAddr = brokerData.getBrokerAddrs().get(MixAll.MASTER_ID);
                  if (masterAddr != null) {
                      BrokerLiveInfo brokerLiveInfo = this.brokerLiveTable.get(masterAddr);
                      if (brokerLiveInfo != null) {
                          result.setHaServerAddr(brokerLiveInfo.getHaServerAddr());
                          result.setMasterAddr(masterAddr);
                      }
                  }
              }
          } finally {
              this.lock.writeLock().unlock();
          }
      } catch (Exception e) {
          log.error("registerBroker Exception", e);
      }

      return result;
  }

    
  /**
   * 功能描述: <br>
   * @author: chenjy
   * @date: 2018年3月5日 下午3:18:59 
   * @param brokerName
   * @param topicConfig
   */
  private void createAndUpdateQueueData(final String brokerName, final TopicConfig topicConfig) {
    
      QueueData queueData = new QueueData();
      queueData.setBrokerName(brokerName);
      queueData.setWriteQueueNums(topicConfig.getWriteQueueNums());
      queueData.setReadQueueNums(topicConfig.getReadQueueNums());
      queueData.setPerm(topicConfig.getPerm());
      queueData.setTopicSynFlag(topicConfig.getTopicSysFlag());

      List<QueueData> queueDataList = this.topicQueueTable.get(topicConfig.getTopicName());
      if (null == queueDataList) {
          queueDataList = new LinkedList<QueueData>();
          queueDataList.add(queueData);
          this.topicQueueTable.put(topicConfig.getTopicName(), queueDataList);
          log.info("new topic registered, {} {}", topicConfig.getTopicName(), queueData);
      } else {
          boolean addNewOne = true;

          Iterator<QueueData> it = queueDataList.iterator();
          while (it.hasNext()) {
              QueueData qd = it.next();
              if (qd.getBrokerName().equals(brokerName)) {
                  if (qd.equals(queueData)) {
                      addNewOne = false;
                  } else {
                      log.info("topic changed, {} OLD: {} NEW: {}", topicConfig.getTopicName(), qd,
                          queueData);
                      it.remove();
                  }
              }
          }

          if (addNewOne) {
              queueDataList.add(queueData);
          }
      }
  }

  public void unregisterBroker(
      final String clusterName,
      final String brokerAddr,
      final String brokerName,
      final long brokerId) {
      try {
          try {
              this.lock.writeLock().lockInterruptibly();
              BrokerLiveInfo brokerLiveInfo = this.brokerLiveTable.remove(brokerAddr);
              log.info("unregisterBroker, remove from brokerLiveTable {}, {}",
                  brokerLiveInfo != null ? "OK" : "Failed",
                  brokerAddr
              );

              boolean removeBrokerName = false;
              BrokerData brokerData = this.brokerAddrTable.get(brokerName);
              if (null != brokerData) {
                  String addr = brokerData.getBrokerAddrs().remove(brokerId);
                  log.info("unregisterBroker, remove addr from brokerAddrTable {}, {}",
                      addr != null ? "OK" : "Failed",
                      brokerAddr
                  );

                  if (brokerData.getBrokerAddrs().isEmpty()) {
                      this.brokerAddrTable.remove(brokerName);
                      log.info("unregisterBroker, remove name from brokerAddrTable OK, {}",
                          brokerName
                      );

                      removeBrokerName = true;
                  }
              }

              if (removeBrokerName) {
                  Set<String> nameSet = this.clusterAddrTable.get(clusterName);
                  if (nameSet != null) {
                      boolean removed = nameSet.remove(brokerName);
                      log.info("unregisterBroker, remove name from clusterAddrTable {}, {}",
                          removed ? "OK" : "Failed",
                          brokerName);

                      if (nameSet.isEmpty()) {
                          this.clusterAddrTable.remove(clusterName);
                          log.info("unregisterBroker, remove cluster from clusterAddrTable {}",
                              clusterName
                          );
                      }
                  }
                  this.removeTopicByBrokerName(brokerName);
              }
          } finally {
              this.lock.writeLock().unlock();
          }
      } catch (Exception e) {
          log.error("unregisterBroker Exception", e);
      }
  }

  
  
  
  private void removeTopicByBrokerName(final String brokerName) {
      Iterator<Entry<String, List<QueueData>>> itMap = this.topicQueueTable.entrySet().iterator();
      while (itMap.hasNext()) {
          Entry<String, List<QueueData>> entry = itMap.next();

          String topic = entry.getKey();
          List<QueueData> queueDataList = entry.getValue();
          Iterator<QueueData> it = queueDataList.iterator();
          while (it.hasNext()) {
              QueueData qd = it.next();
              if (qd.getBrokerName().equals(brokerName)) {
                  log.info("removeTopicByBrokerName, remove one broker's topic {} {}", topic, qd);
                  it.remove();
              }
          }

          if (queueDataList.isEmpty()) {
              log.info("removeTopicByBrokerName, remove the topic all queue {}", topic);
              itMap.remove();
          }
      }
  }

  public TopicRouteData pickupTopicRouteData(final String topic) {
      TopicRouteData topicRouteData = new TopicRouteData();
      boolean foundQueueData = false;
      boolean foundBrokerData = false;
      Set<String> brokerNameSet = new HashSet<String>();
      List<BrokerData> brokerDataList = new LinkedList<BrokerData>();
      topicRouteData.setBrokerDatas(brokerDataList);

      HashMap<String, List<String>> filterServerMap = new HashMap<String, List<String>>();
      topicRouteData.setFilterServerTable(filterServerMap);

      try {
          try {
              this.lock.readLock().lockInterruptibly();
              List<QueueData> queueDataList = this.topicQueueTable.get(topic);
              if (queueDataList != null) {
                  topicRouteData.setQueueDatas(queueDataList);
                  foundQueueData = true;

                  Iterator<QueueData> it = queueDataList.iterator();
                  while (it.hasNext()) {
                      QueueData qd = it.next();
                      brokerNameSet.add(qd.getBrokerName());
                  }

                  for (String brokerName : brokerNameSet) {
                      BrokerData brokerData = this.brokerAddrTable.get(brokerName);
                      if (null != brokerData) {
                          BrokerData brokerDataClone = new BrokerData(brokerData.getCluster(), brokerData.getBrokerName(), (HashMap<Long, String>) brokerData
                              .getBrokerAddrs().clone());
                          brokerDataList.add(brokerDataClone);
                          foundBrokerData = true;
                          for (final String brokerAddr : brokerDataClone.getBrokerAddrs().values()) {
                              List<String> filterServerList = this.filterServerTable.get(brokerAddr);
                              filterServerMap.put(brokerAddr, filterServerList);
                          }
                      }
                  }
              }
          } finally {
              this.lock.readLock().unlock();
          }
      } catch (Exception e) {
          log.error("pickupTopicRouteData Exception", e);
      }

      if (log.isDebugEnabled()) {
          log.debug("pickupTopicRouteData {} {}", topic, topicRouteData);
      }

      if (foundBrokerData && foundQueueData) {
          return topicRouteData;
      }

      return null;
  }

  
  
  
  public void scanNotActiveBroker() {
      Iterator<Entry<String, BrokerLiveInfo>> it = this.brokerLiveTable.entrySet().iterator();
      while (it.hasNext()) {
          Entry<String, BrokerLiveInfo> next = it.next();
          long last = next.getValue().getLastUpdateTimestamp();
          if ((last + BROKER_CHANNEL_EXPIRED_TIME) < System.currentTimeMillis()) {
              RemotingUtil.closeChannel(next.getValue().getChannel());
              it.remove();
              log.warn("The broker channel expired, {} {}ms", next.getKey(), BROKER_CHANNEL_EXPIRED_TIME);
              this.onChannelDestroy(next.getKey(), next.getValue().getChannel());
          }
      }
  }

  public void onChannelDestroy(String remoteAddr, Channel channel) {
      String brokerAddrFound = null;
      if (channel != null) {
          try {
              try {
                  this.lock.readLock().lockInterruptibly();
                  Iterator<Entry<String, BrokerLiveInfo>> itBrokerLiveTable =
                      this.brokerLiveTable.entrySet().iterator();
                  while (itBrokerLiveTable.hasNext()) {
                      Entry<String, BrokerLiveInfo> entry = itBrokerLiveTable.next();
                      if (entry.getValue().getChannel() == channel) {
                          brokerAddrFound = entry.getKey();
                          break;
                      }
                  }
              } finally {
                  this.lock.readLock().unlock();
              }
          } catch (Exception e) {
              log.error("onChannelDestroy Exception", e);
          }
      }

      if (null == brokerAddrFound) {
          brokerAddrFound = remoteAddr;
      } else {
          log.info("the broker's channel destroyed, {}, clean it's data structure at once", brokerAddrFound);
      }

      if (brokerAddrFound != null && brokerAddrFound.length() > 0) {

          try {
              try {
                  this.lock.writeLock().lockInterruptibly();
                  this.brokerLiveTable.remove(brokerAddrFound);
                  this.filterServerTable.remove(brokerAddrFound);
                  String brokerNameFound = null;
                  boolean removeBrokerName = false;
                  Iterator<Entry<String, BrokerData>> itBrokerAddrTable =
                      this.brokerAddrTable.entrySet().iterator();
                  while (itBrokerAddrTable.hasNext() && (null == brokerNameFound)) {
                      BrokerData brokerData = itBrokerAddrTable.next().getValue();

                      Iterator<Entry<Long, String>> it = brokerData.getBrokerAddrs().entrySet().iterator();
                      while (it.hasNext()) {
                          Entry<Long, String> entry = it.next();
                          Long brokerId = entry.getKey();
                          String brokerAddr = entry.getValue();
                          if (brokerAddr.equals(brokerAddrFound)) {
                              brokerNameFound = brokerData.getBrokerName();
                              it.remove();
                              log.info("remove brokerAddr[{}, {}] from brokerAddrTable, because channel destroyed",
                                  brokerId, brokerAddr);
                              break;
                          }
                      }

                      if (brokerData.getBrokerAddrs().isEmpty()) {
                          removeBrokerName = true;
                          itBrokerAddrTable.remove();
                          log.info("remove brokerName[{}] from brokerAddrTable, because channel destroyed",
                              brokerData.getBrokerName());
                      }
                  }

                  if (brokerNameFound != null && removeBrokerName) {
                      Iterator<Entry<String, Set<String>>> it = this.clusterAddrTable.entrySet().iterator();
                      while (it.hasNext()) {
                          Entry<String, Set<String>> entry = it.next();
                          String clusterName = entry.getKey();
                          Set<String> brokerNames = entry.getValue();
                          boolean removed = brokerNames.remove(brokerNameFound);
                          if (removed) {
                              log.info("remove brokerName[{}], clusterName[{}] from clusterAddrTable, because channel destroyed",
                                  brokerNameFound, clusterName);

                              if (brokerNames.isEmpty()) {
                                  log.info("remove the clusterName[{}] from clusterAddrTable, because channel destroyed and no broker in this cluster",
                                      clusterName);
                                  it.remove();
                              }

                              break;
                          }
                      }
                  }

                  if (removeBrokerName) {
                      Iterator<Entry<String, List<QueueData>>> itTopicQueueTable =
                          this.topicQueueTable.entrySet().iterator();
                      while (itTopicQueueTable.hasNext()) {
                          Entry<String, List<QueueData>> entry = itTopicQueueTable.next();
                          String topic = entry.getKey();
                          List<QueueData> queueDataList = entry.getValue();

                          Iterator<QueueData> itQueueData = queueDataList.iterator();
                          while (itQueueData.hasNext()) {
                              QueueData queueData = itQueueData.next();
                              if (queueData.getBrokerName().equals(brokerNameFound)) {
                                  itQueueData.remove();
                                  log.info("remove topic[{} {}], from topicQueueTable, because channel destroyed",
                                      topic, queueData);
                              }
                          }

                          if (queueDataList.isEmpty()) {
                              itTopicQueueTable.remove();
                              log.info("remove topic[{}] all queue, from topicQueueTable, because channel destroyed",
                                  topic);
                          }
                      }
                  }
              } finally {
                  this.lock.writeLock().unlock();
              }
          } catch (Exception e) {
              log.error("onChannelDestroy Exception", e);
          }
      }
  }


}



class BrokerLiveInfo {
  
  private long lastUpdateTimestamp;
  private DataVersion dataVersion;
  private Channel channel;
  private String haServerAddr;

  public BrokerLiveInfo(long lastUpdateTimestamp, DataVersion dataVersion, Channel channel,
      String haServerAddr) {
      this.lastUpdateTimestamp = lastUpdateTimestamp;
      this.dataVersion = dataVersion;
      this.channel = channel;
      this.haServerAddr = haServerAddr;
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

  @Override
  public String toString() {
      return "BrokerLiveInfo [lastUpdateTimestamp=" + lastUpdateTimestamp + ", dataVersion=" + dataVersion
          + ", channel=" + channel + ", haServerAddr=" + haServerAddr + "]";
  }
}
