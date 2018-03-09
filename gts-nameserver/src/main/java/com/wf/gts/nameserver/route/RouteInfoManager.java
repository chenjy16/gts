package com.wf.gts.nameserver.route;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.remoting.protocol.BrokerLiveInfo;
import com.wf.gts.remoting.protocol.ClusterInfo;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.TopicConfigSerializeWrapper;
import com.wf.gts.remoting.util.RemotingUtil;
import io.netty.channel.Channel;


public class RouteInfoManager {
  
  private static final Logger log = LoggerFactory.getLogger(RouteInfoManager.class);
  private final static long BROKER_CHANNEL_EXPIRED_TIME = 1000 * 60 * 2;//两分钟
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final HashMap<String/* brokerAddr */, BrokerLiveInfo> brokerLiveTable;

  
  public RouteInfoManager() {
      this.brokerLiveTable = new HashMap<String, BrokerLiveInfo>(256);
  }
  
  /**
   * 功能描述: 返回gtsManager地址
   * @author: chenjy
   * @date: 2018年3月9日 下午1:46:03 
   * @return
   */
  public byte[] getGtsManagerInfo() {
      ClusterInfo clusterInfoSerializeWrapper = new ClusterInfo();
      Collection<BrokerLiveInfo> brokerLiveSet=this.brokerLiveTable.values();
      clusterInfoSerializeWrapper.setBrokerLiveSet(brokerLiveSet);
      return clusterInfoSerializeWrapper.encode();
  }

  
  /**
   * 功能描述: 注册gts服务
   * @author: chenjy
   * @date: 2018年3月9日 下午3:01:18 
   * @param brokerAddr
   * @param brokerName
   * @param brokerId
   * @param haServerAddr
   * @param topicConfigWrapper
   * @param channel
   * @return
   */
  public RegisterBrokerResult registerBroker(final String brokerAddr,final String brokerName,final long brokerId,final String haServerAddr,final TopicConfigSerializeWrapper topicConfigWrapper,final Channel channel) {
      RegisterBrokerResult result = new RegisterBrokerResult();
      try {
            this.lock.writeLock().lockInterruptibly();
            //记录gtsmanage地址和ha地址
            BrokerLiveInfo prevBrokerLiveInfo = this.brokerLiveTable.put(brokerAddr,new BrokerLiveInfo(brokerId,System.currentTimeMillis(),topicConfigWrapper.getDataVersion(),channel,haServerAddr,brokerName,brokerAddr));
            if (Objects.isNull(prevBrokerLiveInfo)) {
                log.info("new broker registered, {} HAServer: {}", brokerAddr, haServerAddr);
            }
      } catch (Exception e) {
          log.error("registerBroker Exception", e);
      }finally {
        this.lock.writeLock().unlock();
      }

      return result;
  }

    
  
  
  /**
   * 功能描述: 注销gts服务
   * @author: chenjy
   * @date: 2018年3月7日 下午4:29:31 
   * @param clusterName
   * @param brokerAddr
   * @param brokerName
   * @param brokerId
   */
  public void unregisterBroker(final String brokerAddr,final String brokerName,final long brokerId) {
      try {
            this.lock.writeLock().lockInterruptibly();
            BrokerLiveInfo brokerLiveInfo = this.brokerLiveTable.remove(brokerAddr);
            log.info("unregisterBroker, remove from brokerLiveTable {}, {}",brokerLiveInfo != null ? "OK" : "Failed",brokerAddr);
      } catch (Exception e) {
            log.error("unregisterBroker Exception", e);
      }finally {
            this.lock.writeLock().unlock();
      }
  }

  
  /**
   * 功能描述: 扫描不活跃的连接
   * @author: chenjy
   * @date: 2018年3月8日 下午1:35:37
   */
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

  
  
  /**
   * 功能描述: 
   * @author: chenjy
   * @date: 2018年3月8日 下午1:36:01 
   * @param remoteAddr
   * @param channel
   */
  public void onChannelDestroy(String remoteAddr, Channel channel) {
        String brokerAddrFound = null;
        if (Objects.nonNull(channel)) {
              try {
                    this.lock.readLock().lockInterruptibly();
                    Iterator<Entry<String, BrokerLiveInfo>> itBrokerLiveTable =this.brokerLiveTable.entrySet().iterator();
                    while (itBrokerLiveTable.hasNext()) {
                        Entry<String, BrokerLiveInfo> entry = itBrokerLiveTable.next();
                        if (entry.getValue().getChannel() == channel) {
                            brokerAddrFound = entry.getKey();
                            break;
                        }
                    }
              } catch (Exception e) {
                  log.error("onChannelDestroy Exception", e);
              }finally {
                this.lock.readLock().unlock();
              }
        }
        if (Objects.isNull(brokerAddrFound)) {
            brokerAddrFound = remoteAddr;
        } else {
            log.info("the broker's channel destroyed, {}, clean it's data structure at once", brokerAddrFound);
        }
        if (Objects.nonNull(brokerAddrFound)&& brokerAddrFound.length() > 0) {
              try {
                    this.lock.writeLock().lockInterruptibly();
                    this.brokerLiveTable.remove(brokerAddrFound);
              } catch (Exception e) {
                  log.error("onChannelDestroy Exception", e);
              }finally {
                this.lock.writeLock().unlock();
              }
        }
    }
}
