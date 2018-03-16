package com.wf.gts.core.client;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.core.config.TxConfig;
import com.wf.gts.core.exception.MQBrokerException;
import com.wf.gts.remoting.RPCHook;
import com.wf.gts.remoting.exception.RemotingException;
import com.wf.gts.remoting.header.UnregisterClientRequestHeader;
import com.wf.gts.remoting.netty.NettyClientConfig;
import com.wf.gts.remoting.protocol.BrokerLiveInfo;
import com.wf.gts.remoting.protocol.ClusterInfo;
import com.wf.gts.remoting.protocol.HeartbeatData;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;

public class MQClientInstance {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(MQClientInstance.class);
    private final static long LOCK_TIMEOUT_MILLIS = 3000;
    private final MQClientAPIImpl mQClientAPIImpl;
    private final Lock lockNamesrv = new ReentrantLock();
    private final Lock lockHeartbeat = new ReentrantLock();
    private final NettyClientConfig nettyClientConfig;
    private final AtomicReference<BrokerLiveInfo> brokerAddrTable=new AtomicReference<BrokerLiveInfo>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "MQClientFactoryScheduledThread");
        }
    });
    
    
    public MQClientInstance(TxConfig txConfig, RPCHook rpcHook) {
        this.nettyClientConfig = new NettyClientConfig();
        this.nettyClientConfig.setClientCallbackExecutorThreads(txConfig.getClientCallbackExecutorThreads());
        this.nettyClientConfig.setUseTLS(txConfig.isUseTLS());
        this.mQClientAPIImpl = new MQClientAPIImpl(this.nettyClientConfig, rpcHook,new ClientRemotingProcessor(this));
    }

    public void start()  {
      this.mQClientAPIImpl.start();
      this.startScheduledTask();
    }
    
    
    public void shutdown() {
      this.scheduledExecutorService.shutdown();
      unregisterClientWithLock();
      this.mQClientAPIImpl.shutdown();
    }
    
    
    private void startScheduledTask() {
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    MQClientInstance.this.updateTopicRouteInfoFromNameServer();
                } catch (Exception e) {
                  LOGGER.error("ScheduledTask updateTopicRouteInfoFromNameServer exception", e);
                }
            }
        }, 10, 30, TimeUnit.MILLISECONDS);
        
        
        
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
          
          @Override
          public void run() {
                try {
                   // MQClientInstance.this.cleanOfflineBroker();
                    MQClientInstance.this.sendHeartbeatToAllBrokerWithLock();
                } catch (Exception e) {
                    LOGGER.error("ScheduledTask sendHeartbeatToAllBroker exception", e);
                }
            }
         }, 1000, 10, TimeUnit.MILLISECONDS);
        
        
        
        

    }
    
    /**
     * 功能描述: 用锁注销客户端
     * @author: chenjy
     * @date: 2018年3月16日 上午9:53:34
     */
    private void unregisterClientWithLock() {
      try {
          if (this.lockHeartbeat.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
              try {
                  this.unregisterClient("", "", "","", 10);
              } catch (Exception e) {
                LOGGER.error("unregisterClient exception", e);
              } finally {
                  this.lockHeartbeat.unlock();
              }
          } else {
            LOGGER.warn("lock heartBeat, but failed.");
          }
      } catch (InterruptedException e) {
        LOGGER.warn("unregisterClientWithLock exception", e);
      }
  }
    
    
    /**
     * 功能描述: 注销客户端
     * @author: chenjy
     * @date: 2018年3月16日 上午9:44:01 
     * @param addr
     * @param clientID
     * @param producerGroup
     * @param consumerGroup
     * @param timeoutMillis
     * @throws RemotingException
     * @throws MQBrokerException
     * @throws InterruptedException
     */
    public void unregisterClient(String addr,String clientID,String producerGroup,String consumerGroup,long timeoutMillis) throws RemotingException, MQBrokerException, InterruptedException {
       
        UnregisterClientRequestHeader requestHeader = new UnregisterClientRequestHeader();
        requestHeader.setClientID(clientID);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.UNREGISTER_CLIENT, requestHeader);
        RemotingCommand response = this.mQClientAPIImpl.sendMessageSync(addr,timeoutMillis,request);
        assert response != null;
        switch (response.getCode()) {
            case ResponseCode.SUCCESS: {
                return;
            }
            default:
                break;
        }
        throw new MQBrokerException(response.getCode(), response.getRemark());
    }
    
    
    
    
    
    
    public void sendHeartbeatToAllBrokerWithLock() {
        if (this.lockHeartbeat.tryLock()) {
            try {
                this.sendHeartbeatToAllBroker();
            } catch (final Exception e) {
                LOGGER.error("sendHeartbeatToAllBroker exception", e);
            } finally {
                this.lockHeartbeat.unlock();
            }
        } else {
            LOGGER.warn("lock heartBeat, but failed.");
        }
    }
    
    
    private void sendHeartbeatToAllBroker() {
      HeartbeatData heartbeatData = new HeartbeatData();
      heartbeatData.setClientID("");
      if (this.brokerAddrTable.get()!=null) {
          try {
              int version = this.mQClientAPIImpl.sendHearbeat("", heartbeatData, 3000);
          } catch (Exception e) {
              LOGGER.info("send heart beat to broker[{} {} {}] exception, because the broker not up, forget it");
          }
      }
   }
      
    
    
    public boolean updateTopicRouteInfoFromNameServer() {
        try {
            if (this.lockNamesrv.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    ClusterInfo clusterInfo=this.mQClientAPIImpl.getBrokerClusterInfo("localhost:9876",1000 * 3);
                    if (Objects.nonNull(clusterInfo)&& (!clusterInfo.getBrokerLiveSet().isEmpty())) {
                      brokerAddrTable.set(clusterInfo.getBrokerLiveSet().stream().filter(item->Objects.nonNull(item)).findFirst().get());
                      return true;
                    } else {
                      LOGGER.warn("updateTopicRouteInfoFromNameServer, getBrokerClusterInfo return null");
                    }
                } catch (Exception e) {
                      LOGGER.warn("getBrokerClusterInfo Exception", e);
                } finally {
                    this.lockNamesrv.unlock();
                }
            } else {
              LOGGER.warn("updateTopicRouteInfoFromNameServer tryLock timeout {}ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException e) {
          LOGGER.warn("updateTopicRouteInfoFromNameServer Exception", e);
        }
        return false;
    }

    public AtomicReference<BrokerLiveInfo> getBrokerAddrTable() {
      return brokerAddrTable;
    }

    public MQClientAPIImpl getmQClientAPIImpl() {
      return mQClientAPIImpl;
    }
 
}
