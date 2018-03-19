package com.wf.gts.manage.client;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.manage.client.ClientChannelInfo;
import com.wf.gts.remoting.core.RemotingHelper;
import com.wf.gts.remoting.core.RemotingUtil;

import io.netty.channel.Channel;

public class ProducerManager {
  
  private static final Logger log = LoggerFactory.getLogger(ProducerManager.class);
  private static final long LOCK_TIMEOUT_MILLIS = 3000;
  private static final long CHANNEL_EXPIRED_TIMEOUT = 1000 * 120;
  private final Lock groupChannelLock = new ReentrantLock();
  private final HashMap<String /* remoteAddr */, ClientChannelInfo> groupChannelTable =new HashMap<String,ClientChannelInfo>();

  public ProducerManager() {
  }

  
  public HashMap<String, ClientChannelInfo> getGroupChannelTable() {
      HashMap<String /* remoteAddr */, ClientChannelInfo> newGroupChannelTable =
          new HashMap<String, ClientChannelInfo>();
      try {
          if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
              try {
                  newGroupChannelTable.putAll(groupChannelTable);
              } finally {
                  groupChannelLock.unlock();
              }
          }
      } catch (InterruptedException e) {
          log.error("", e);
      }
      return newGroupChannelTable;
  }

  
  public void scanNotActiveChannel() {
      try {
          if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
              try {
                    Iterator<Entry<String,  ClientChannelInfo>> it = this.groupChannelTable.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<String,  ClientChannelInfo> item = it.next();
                        final ClientChannelInfo info = item.getValue();
                        long diff = System.currentTimeMillis() - info.getLastUpdateTimestamp();
                        if (diff > CHANNEL_EXPIRED_TIMEOUT) {
                            it.remove();
                            log.warn("SCAN: remove expired channel[{}] from ProducerManager groupChannelTable",
                                RemotingHelper.parseChannelRemoteAddr(info.getChannel()));
                            RemotingUtil.closeChannel(info.getChannel());
                        }
                    }
              } finally {
                  this.groupChannelLock.unlock();
              }
          } else {
              log.warn("ProducerManager scanNotActiveChannel lock timeout");
          }
      } catch (InterruptedException e) {
          log.error("", e);
      }
  }

  
  public void doChannelCloseEvent(final String remoteAddr, final Channel channel) {
      if (channel != null) {
          try {
              if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                  try {
                    Iterator<Entry<String,  ClientChannelInfo>> it =this.groupChannelTable.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<String,ClientChannelInfo> item = it.next();
                        ClientChannelInfo clientChannelInfo = item.getValue();
                        //待验证
                        if(channel==clientChannelInfo.getChannel()){
                          it.remove();
                          log.info( "NETTY EVENT: remove channel[{}][{}] from ProducerManager groupChannelTable",clientChannelInfo.toString(), remoteAddr);
                        }
                    }  
                  } finally {
                      this.groupChannelLock.unlock();
                  }
              } else {
                  log.warn("ProducerManager doChannelCloseEvent lock timeout");
              }
          } catch (InterruptedException e) {
              log.error("", e);
          }
      }
  }

  public void registerProducer(final String remoteAddr, final ClientChannelInfo clientChannelInfo) {
      try {
          if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
              try {
                  ClientChannelInfo channelInfo = this.groupChannelTable.get(remoteAddr);
                  if (null == channelInfo) {
                      this.groupChannelTable.put(remoteAddr, clientChannelInfo);
                      log.info("new producer connected, group: {} channel: {}", remoteAddr,
                          clientChannelInfo.toString());
                  }else{
                    channelInfo.setLastUpdateTimestamp(System.currentTimeMillis());
                  }
              } finally {
                  this.groupChannelLock.unlock();
              }
          } else {
              log.warn("ProducerManager registerProducer lock timeout");
          }
      } catch (InterruptedException e) {
          log.error("", e);
      }
  }

  public void unregisterProducer(final String remoteAddr, final ClientChannelInfo clientChannelInfo) {
      try {
          if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
              try {
                    ClientChannelInfo old = this.groupChannelTable.remove(clientChannelInfo.getChannel().remoteAddress().toString());
                    if (old != null) {
                        log.info("unregister a producer[{}] from groupChannelTable {}", remoteAddr,
                            clientChannelInfo.toString());
                    }
              } finally {
                  this.groupChannelLock.unlock();
              }
          } else {
              log.warn("ProducerManager unregisterProducer lock timeout");
          }
      } catch (InterruptedException e) {
          log.error("", e);
      }
  }

}
