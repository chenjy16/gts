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

public class ClientChannelManager {
  
  private static final Logger log = LoggerFactory.getLogger(ClientChannelManager.class);
  private static final long LOCK_TIMEOUT_MILLIS = 3000;
  private static final long CHANNEL_EXPIRED_TIMEOUT = 1000 * 120;
  private final Lock groupChannelLock = new ReentrantLock();
  private final HashMap<String /* remoteAddr */, ClientChannelInfo> groupChannelTable =new HashMap<String,ClientChannelInfo>();

  
  
  public HashMap<String, ClientChannelInfo> getAllClientChannelTable() {
      HashMap<String /* remoteAddr */, ClientChannelInfo> newGroupChannelTable =new HashMap<String, ClientChannelInfo>();
      try {
          if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
              try {
                  newGroupChannelTable.putAll(this.groupChannelTable);
              } finally {
                  groupChannelLock.unlock();
              }
          }
      } catch (InterruptedException e) {
          log.error("获取客户端channel集合异常:{}", e);
      }
      return newGroupChannelTable;
  }

  
  public void scanNotActiveChannel() {
      try {
          if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
              Iterator<Entry<String,  ClientChannelInfo>> it = this.groupChannelTable.entrySet().iterator();
              while (it.hasNext()) {
                  Entry<String,  ClientChannelInfo> item = it.next();
                  final ClientChannelInfo info = item.getValue();
                  long diff = System.currentTimeMillis() - info.getLastUpdateTimestamp();
                  if (diff > CHANNEL_EXPIRED_TIMEOUT) {
                      it.remove();
                      log.warn("移除过期的channel:{}",
                          RemotingHelper.parseChannelRemoteAddr(info.getChannel()));
                      RemotingUtil.closeChannel(info.getChannel());
                  }
              }
          } else {
              log.warn("移除过期的channel锁超时");
          }
      } catch (Exception e) {
          log.error("扫描不活跃的channel异常:{}", e);
      } finally {
          this.groupChannelLock.unlock();
      }
  }

  
  
    public void doChannelCloseEvent(final String remoteAddr, final Channel channel) {
        if (channel != null) {
            try {
                if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
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
                } else {
                    log.warn("doChannelCloseEvent锁超时");
                }
            } catch (Exception e) {
                log.error("doChannelCloseEvent异常:{}", e);
            }finally {
              this.groupChannelLock.unlock();
            }
        }
    }

    public void registerProducer(String remoteAddr, ClientChannelInfo clientChannelInfo) throws InterruptedException {
            if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    ClientChannelInfo channelInfo = this.groupChannelTable.get(remoteAddr);
                    if (null == channelInfo) {
                        this.groupChannelTable.put(remoteAddr, clientChannelInfo);
                        log.info("注册客户端信息:{}",remoteAddr);
                    }else{
                      channelInfo.setLastUpdateTimestamp(System.currentTimeMillis());
                    }
                } finally {
                    this.groupChannelLock.unlock();
                }
            } else {
                log.warn("注册客户端锁超时");
            }
    }

    public void unregisterProducer(String remoteAddr, ClientChannelInfo clientChannelInfo) throws InterruptedException {
          if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
              try {
                    log.info("注销客户端信息:{}",remoteAddr);
                    ClientChannelInfo old = this.groupChannelTable.remove(remoteAddr);
                    if (old != null) {
                      log.info("注销客户端信息:{}",remoteAddr);
                    }
              } finally {
                  this.groupChannelLock.unlock();
              }
          } else {
              log.warn("注销客户端锁超时");
          }
    }

}
