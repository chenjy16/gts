package com.wf.gts.manage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wf.gts.manage.client.ClientChannelManager;
import com.wf.gts.manage.client.ClientHousekeepingService;
import com.wf.gts.manage.config.GtsManageConfig;
import com.wf.gts.manage.executer.TxTransactionExecutor;
import com.wf.gts.manage.out.ManageOuterAPI;
import com.wf.gts.manage.processor.ClientManageProcessor;
import com.wf.gts.manage.processor.DefaultManageProcessor;
import com.wf.gts.manage.service.TxManagerService;
import com.wf.gts.remoting.RemotingServer;
import com.wf.gts.remoting.core.RemotingUtil;
import com.wf.gts.remoting.core.ThreadFactoryImpl;
import com.wf.gts.remoting.netty.NettyClientConfig;
import com.wf.gts.remoting.netty.NettyRemotingServer;
import com.wf.gts.remoting.netty.NettyServerConfig;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.RequestCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ManageController  {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageController.class);
    private RemotingServer remotingServer;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("GtsScheduledThread"));
    private ManageOuterAPI manageOuterAPI;
    private ExecutorService defaultExecutor;
    private BlockingQueue<Runnable> clientManagerThreadPoolQueue;
    private ExecutorService clientManageExecutor;
    private ClientChannelManager clientChannelManager;
    private ClientHousekeepingService clientHousekeepingService;
    
    @Autowired
    private TxManagerService txManagerService;
    
    @Autowired
    private TxTransactionExecutor txTransactionExecutor;
    
    @Autowired
    NettyServerConfig nettyServerConfig;
    
    @Autowired
    NettyClientConfig nettyClientConfig;
    
    @Autowired
    GtsManageConfig gtsManageCfg;
    
    
    
    public void start() {
        initialize();
        manageOuterAPI.start();
        remotingServer.start();
        manageOuterAPI.updateNameServerAddressList(gtsManageCfg.getNamesrvAddr());
        registerBrokerAll();
        scheduledExecutorService.scheduleAtFixedRate(()->{
            try {
              ManageController.this.registerBrokerAll();
            } catch (Throwable e) {
                LOGGER.error("registerManageAll Exception",e);
            }
        }, 1000 * 10, 1000 * 30, TimeUnit.MILLISECONDS);
    }
    
    
    
    
    /**
     * 功能描述: 定时任务初始化
     * @author: chenjy
     * @date: 2018年3月14日 下午1:22:32 
     * @return
     */
    private void initialize(){
      defaultExecutor=Executors.newFixedThreadPool(gtsManageCfg.getDefaultThreadPoolNums() , new ThreadFactoryImpl("defaultThread_"));
      clientManagerThreadPoolQueue = new LinkedBlockingQueue<Runnable>(gtsManageCfg.getClientManagerThreadPoolQueueCapacity());
      clientManageExecutor = new ThreadPoolExecutor(gtsManageCfg.getClientManageThreadPoolNums(), gtsManageCfg.getClientManageThreadPoolNums(),
          1000 * 60,TimeUnit.MILLISECONDS, clientManagerThreadPoolQueue,new ThreadFactoryImpl("ClientManageThread_"));
      
      manageOuterAPI=new ManageOuterAPI(nettyClientConfig);
      clientChannelManager = new ClientChannelManager();
      clientHousekeepingService= new ClientHousekeepingService(clientChannelManager);
      remotingServer = new NettyRemotingServer(nettyServerConfig,clientHousekeepingService);
      registerProcessor();
    }
    
    
    
    private void registerProcessor() {
      ClientManageProcessor clientProcessor = new ClientManageProcessor(clientChannelManager);
      remotingServer.registerProcessor(RequestCode.HEART_BEAT, clientProcessor,clientManageExecutor);
      remotingServer.registerProcessor(RequestCode.UNREGISTER_CLIENT, clientProcessor, clientManageExecutor);
      remotingServer.registerDefaultProcessor(new DefaultManageProcessor(this), defaultExecutor);
    }

    
    
    public void stop() {
      
        if(clientHousekeepingService != null) {
            clientHousekeepingService.shutdown();
        }
        if (remotingServer != null) {
            remotingServer.shutdown();
        }
        scheduledExecutorService.shutdown();
        try {
            scheduledExecutorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          
        }
        unregisterBrokerAll();
        if (manageOuterAPI != null) {
          manageOuterAPI.shutdown();
        }
      
    }
    
    /**
     * 功能描述: 注册nameserver服务器
     * @author: chenjy
     * @date: 2018年3月21日 下午5:35:34
     */
    public synchronized void registerBrokerAll() {
        RegisterBrokerResult registerBrokerResult = manageOuterAPI.registerManageAll(
            getLocalAddr(),
            gtsManageCfg.getManageName(),
            gtsManageCfg.getManageId(),
            gtsManageCfg.getRegisterBrokerTimeoutMills());
        
        
    }
    
    
    /**
     * 功能描述: 注销nameserver服务器
     * @author: chenjy
     * @date: 2018年3月14日 上午11:36:04
     */
    private void unregisterBrokerAll() {
      this.manageOuterAPI.unregisterManageAll(getLocalAddr(),
          gtsManageCfg.getManageName(),
          gtsManageCfg.getManageId());
    }


    
    private  String getLocalAddr() {
      return RemotingUtil.getLocalAddress()+ ":" + this.nettyServerConfig.getListenPort();
    }
    

}
