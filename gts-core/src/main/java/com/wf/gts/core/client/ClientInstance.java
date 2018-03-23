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
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.wf.gts.core.client.processor.ClientRemotingProcessor;
import com.wf.gts.core.config.ClientConfig;
import com.wf.gts.core.exception.GtsManageException;
import com.wf.gts.remoting.exception.RemotingException;
import com.wf.gts.remoting.header.UnregisterClientRequestHeader;
import com.wf.gts.remoting.netty.NettyClientConfig;
import com.wf.gts.remoting.protocol.LiveManageInfo;
import com.wf.gts.remoting.protocol.HeartbeatData;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;


@Component
public class ClientInstance {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientInstance.class);
    
    private final static long LOCK_TIMEOUT_MILLIS = 3000;
    private final Lock lockNamesrv = new ReentrantLock();
    private final Lock lockHeartbeat = new ReentrantLock();
    
    private  ClientAPIImpl clientAPIImpl;
    private  NettyClientConfig nettyClientConfig;
    private  AtomicReference<LiveManageInfo> liveManageRef=new AtomicReference<LiveManageInfo>();
    private  ClientConfig config;
    
    
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ClientScheduledThread");
        }
    });
    

    
    
    /**
     * 功能描述: 客户端初始化启动
     * @author: chenjy
     * @date: 2018年3月21日 下午2:21:57 
     * @param config
     */
    public void start(ClientConfig config)  {
      initialize(config);
      startScheduledTask();
    }
    
    
    
   private void initialize(ClientConfig config){
     this.config=config;
     this.nettyClientConfig = new NettyClientConfig();
     this.nettyClientConfig.setClientCallbackExecutorThreads(config.getClientCallbackExecutorThreads());
     this.nettyClientConfig.setUseTLS(config.isUseTLS());
     this.clientAPIImpl = new ClientAPIImpl(this.nettyClientConfig, null,new ClientRemotingProcessor());
     clientAPIImpl.start();
     updateRouteInfoFromNameServer();
     sendHeartbeatToAllManageWithLock();
   }
    
   
   /**
    * 功能描述: 客户端关闭
    * @author: chenjy
    * @date: 2018年3月21日 下午2:22:23
    */
   public void shutdown() {
      this.scheduledExecutorService.shutdown();
      unregisterClientWithLock();
      this.clientAPIImpl.shutdown();
   }
    
    
    
    /**
     * 功能描述: 启动各种定时任务
     * @author: chenjy
     * @date: 2018年3月20日 下午1:20:12
     */
    private void startScheduledTask() {
      
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    updateRouteInfoFromNameServer();
                } catch (Exception e) {
                  LOGGER.error("更新客户端路由信息定时任务异常:{}", e);
                }
            }
        }, 10, config.getPollNameServerInterval(), TimeUnit.MILLISECONDS);
        
        
        
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
          @Override
          public void run() {
                try {
                   sendHeartbeatToAllManageWithLock();
                } catch (Exception e) {
                    LOGGER.error("发送心跳信息给manage定时任务异常:{}", e);
                }
            }
         }, 1000, config.getHeartbeatBrokerInterval(), TimeUnit.MILLISECONDS);

    }
    
    
    /**
     * 功能描述: 注销客户端
     * @author: chenjy
     * @date: 2018年3月16日 上午9:53:34
     */
    private void unregisterClientWithLock() {
        try {
            if (this.lockHeartbeat.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
               this.unregisterClient(config.getNamesrvAddr(), config.buildMQClientId(), config.getTimeoutMillis());
            }
        }catch (Exception e) {
            LOGGER.warn("客户端注销异常:{}", e);
        }finally {
            this.lockHeartbeat.unlock();
        }
    }
    

    private void unregisterClient(String addr,String clientID,long timeoutMillis) throws RemotingException, GtsManageException, InterruptedException {
        
        UnregisterClientRequestHeader requestHeader = new UnregisterClientRequestHeader();
        requestHeader.setClientID(clientID);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.UNREGISTER_CLIENT, requestHeader);
        RemotingCommand response = this.clientAPIImpl.sendMessageSync(addr,timeoutMillis,request);
        assert response != null;
        switch (response.getCode()) {
            case ResponseCode.SUCCESS: {
                return;
            }
            default:
                break;
        }
        throw new GtsManageException(response.getCode(), response.getRemark());
    }
    
    
    /**
     * 功能描述: 发送心跳信息给manage
     * @author: chenjy
     * @date: 2018年3月21日 下午2:17:43
     */
    private void sendHeartbeatToAllManageWithLock() {
        if (this.lockHeartbeat.tryLock()) {
            try {
                this.sendHeartbeatToAllManage();
            } catch (Exception e) {
                LOGGER.error("发送心跳给manage异常:{}", e);
            } finally {
                this.lockHeartbeat.unlock();
            }
        } else {
            LOGGER.warn("发送心跳锁失败");
        }
    }
    
    
    private void sendHeartbeatToAllManage() {
      HeartbeatData heartbeatData = new HeartbeatData();
      heartbeatData.setClientID(config.buildMQClientId());
      if (Objects.nonNull( this.liveManageRef.get())) {
          try {
              RemotingCommand res= this.clientAPIImpl.sendHearbeat(this.liveManageRef.get().getGtsManageLiveAddr().getGtsManageAddr(), heartbeatData, config.getTimeoutMillis());
              LOGGER.info("发送心跳信息:{}",JSON.toJSONString(res));
          } catch (Exception e) {
              LOGGER.info("发送心跳异常:{}",e);
          }
      }else{
        
      }
   }
      
    
    /**
     * 功能描述: 更新manage的地址信息
     * @author: chenjy
     * @date: 2018年3月21日 上午10:27:38 
     * @return
     */
    private boolean updateRouteInfoFromNameServer() {
        try {
            if (this.lockNamesrv.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
              
                LiveManageInfo liveManageInfo=this.clientAPIImpl.getGtsClusterInfo(config.getNamesrvAddr(),config.getTimeoutMillis());
                LOGGER.info("更新路由信息:{}",JSON.toJSONString(liveManageInfo));
                this.liveManageRef.set(liveManageInfo);
                return true;
                
            } else {
              LOGGER.warn("更新路由信息 tryLock timeout {} ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (Exception e) {
            LOGGER.error("更新路由信息失败:{}", e);
        }finally {
          this.lockNamesrv.unlock();
        }
        return false;
    }
    
    
    public ClientAPIImpl getClientAPIImpl() {
      return clientAPIImpl;
    }

    public ClientConfig getConfig() {
      return config;
    }

    public AtomicReference<LiveManageInfo> getLiveManageRef() {
      return liveManageRef;
    }

}
