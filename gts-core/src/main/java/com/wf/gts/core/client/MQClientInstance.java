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
import com.wf.gts.remoting.RPCHook;
import com.wf.gts.remoting.netty.NettyClientConfig;
import com.wf.gts.remoting.protocol.BrokerLiveInfo;
import com.wf.gts.remoting.protocol.ClusterInfo;

public class MQClientInstance {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(MQClientInstance.class);
    private final static long LOCK_TIMEOUT_MILLIS = 3000;
    private final MQClientAPIImpl mQClientAPIImpl;
    private final Lock lockNamesrv = new ReentrantLock();
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
        this.mQClientAPIImpl = new MQClientAPIImpl(this.nettyClientConfig, rpcHook);
    }

    public void start()  {
      this.mQClientAPIImpl.start();
      this.startScheduledTask();
    }
    
    
    public void shutdown() {
      this.scheduledExecutorService.shutdown();
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

 
}
