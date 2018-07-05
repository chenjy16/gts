package com.wf.gts.nameserver.route;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wf.gts.remoting.core.RemotingUtil;
import com.wf.gts.remoting.protocol.GtsManageLiveAddr;
import com.wf.gts.remoting.protocol.GtsManageLiveInfo;
import com.wf.gts.remoting.protocol.LiveManageInfo;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.TopicConfigSerializeWrapper;

import io.netty.channel.Channel;


public class RouteInfoManager {
  
  private static final Logger log = LoggerFactory.getLogger(RouteInfoManager.class);
  private final static long CHANNEL_EXPIRED_TIME = 1000 * 60 * 2;//两分钟
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final HashMap<String/* brokerAddr */, GtsManageLiveInfo> liveTable;
  
  public RouteInfoManager() {
      this.liveTable = new HashMap<String, GtsManageLiveInfo>(256);
  }
  
  /**
   * 功能描述: 返回gtsManager地址
   * @author: chenjy
   * @date: 2018年3月9日 下午1:46:03 
   * @return
   */
  public byte[] getGtsManagerInfo() {
    
      LiveManageInfo liveManageInfo = new LiveManageInfo();
      List<GtsManageLiveAddr>  brokerLiveAddrs=this.liveTable.values().stream().
      filter(item->Objects.nonNull(item)).
      map(item-> new GtsManageLiveAddr(item.getGtsManageId(), item.getLastUpdateTimestamp(),item.getGtsManageName(), item.getGtsManageAddr())).
      collect(Collectors.toList());
      liveManageInfo.setGtsManageLiveAddrs(brokerLiveAddrs);
      return liveManageInfo.encode();
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
  public RegisterBrokerResult registerBroker( String brokerAddr, String brokerName, long brokerId, TopicConfigSerializeWrapper topicConfigWrapper, Channel channel) {
      RegisterBrokerResult result = new RegisterBrokerResult();
      try {
            this.lock.writeLock().lockInterruptibly();
            //记录gtsmanage地址
            GtsManageLiveInfo prevBrokerLiveInfo = this.liveTable.put(brokerAddr,new GtsManageLiveInfo(brokerId,System.currentTimeMillis(),topicConfigWrapper.getDataVersion(),channel,brokerName,brokerAddr));
            if (Objects.isNull(prevBrokerLiveInfo)) {
                log.info("new Manage registered, {} ", brokerAddr);
            }
      } catch (Exception e) {
          log.error("registerManage Exception", e);
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
  public void unregisterBroker( String brokerAddr, String brokerName, long brokerId) {
      try {
            this.lock.writeLock().lockInterruptibly();
            GtsManageLiveInfo brokerLiveInfo = this.liveTable.remove(brokerAddr);
            log.info("unregisterManage, remove from liveTable {}, {}",brokerLiveInfo != null ? "OK" : "Failed",brokerAddr);
      } catch (Exception e) {
            log.error("unregisterManage Exception", e);
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
      Iterator<Entry<String, GtsManageLiveInfo>> it = this.liveTable.entrySet().iterator();
      while (it.hasNext()) {
          Entry<String, GtsManageLiveInfo> next = it.next();
          long last = next.getValue().getLastUpdateTimestamp();
          if ((last + CHANNEL_EXPIRED_TIME) < System.currentTimeMillis()) {
              RemotingUtil.closeChannel(next.getValue().getChannel());
              it.remove();
              log.warn("The manage channel expired, {} {}ms", next.getKey(), CHANNEL_EXPIRED_TIME);
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
                    Iterator<Entry<String, GtsManageLiveInfo>> itBrokerLiveTable =this.liveTable.entrySet().iterator();
                    while (itBrokerLiveTable.hasNext()) {
                        Entry<String, GtsManageLiveInfo> entry = itBrokerLiveTable.next();
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
            log.info("the manage channel destroyed, {}, clean it's data structure at once", brokerAddrFound);
        }
        if (Objects.nonNull(brokerAddrFound)&& brokerAddrFound.length() > 0) {
              try {
                    this.lock.writeLock().lockInterruptibly();
                    this.liveTable.remove(brokerAddrFound);
              } catch (Exception e) {
                  log.error("onChannelDestroy Exception", e);
              }finally {
                this.lock.writeLock().unlock();
              }
        }
    }
}
