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
import com.wf.gts.manage.client.ClientHousekeepingService;
import com.wf.gts.manage.client.ProducerManager;
import com.wf.gts.manage.config.GtsManageConfig;
import com.wf.gts.manage.executer.TxTransactionExecutor;
import com.wf.gts.manage.out.ManageOuterAPI;
import com.wf.gts.manage.processor.ClientManageProcessor;
import com.wf.gts.manage.processor.DefaultBrokerProcessor;
import com.wf.gts.manage.service.TxManagerService;
import com.wf.gts.remoting.RemotingServer;
import com.wf.gts.remoting.core.ThreadFactoryImpl;
import com.wf.gts.remoting.netty.NettyClientConfig;
import com.wf.gts.remoting.netty.NettyRemotingServer;
import com.wf.gts.remoting.netty.NettyServerConfig;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.RequestCode;


@Component
public class ManageController  {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageController.class);
    
    private RemotingServer remotingServer;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("GtsScheduledThread"));
    private ManageOuterAPI manageOuterAPI;
    private ExecutorService defaultExecutor;
    private BlockingQueue<Runnable> clientManagerThreadPoolQueue;
    private ExecutorService clientManageExecutor;
    private ProducerManager producerManager;
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
    
    
    /**
     * 功能描述: 定时任务初始化
     * @author: chenjy
     * @date: 2018年3月14日 下午1:22:32 
     * @return
     */
    private boolean initialize(){
      defaultExecutor=Executors.newFixedThreadPool(gtsManageCfg.getDefaultThreadPoolNums() , new ThreadFactoryImpl("defaultThread_"));
      clientManagerThreadPoolQueue = new LinkedBlockingQueue<Runnable>(gtsManageCfg.getClientManagerThreadPoolQueueCapacity());
      clientManageExecutor = new ThreadPoolExecutor(
          gtsManageCfg.getClientManageThreadPoolNums(),
          gtsManageCfg.getClientManageThreadPoolNums(),
          1000 * 60,TimeUnit.MILLISECONDS, 
          clientManagerThreadPoolQueue,
          new ThreadFactoryImpl("ClientManageThread_"));
      
      manageOuterAPI=new ManageOuterAPI(nettyClientConfig);
      producerManager = new ProducerManager();
      clientHousekeepingService= new ClientHousekeepingService(producerManager);
      remotingServer = new NettyRemotingServer(nettyServerConfig,clientHousekeepingService);
      registerProcessor();
      return true;
    }
    
    
    
    private void registerProcessor() {
      ClientManageProcessor clientProcessor = new ClientManageProcessor(producerManager);
      remotingServer.registerProcessor(RequestCode.HEART_BEAT, clientProcessor,clientManageExecutor);
      remotingServer.registerProcessor(RequestCode.UNREGISTER_CLIENT, clientProcessor, clientManageExecutor);
      remotingServer.registerDefaultProcessor(new DefaultBrokerProcessor(this), defaultExecutor);
  }

    
    
    
    /**
     * 启动netty服务
     */
    public void start() {
        initialize();
        manageOuterAPI.start();
        remotingServer.start();
        manageOuterAPI.updateNameServerAddressList(gtsManageCfg.getNamesrvAddr());
        registerBrokerAll(true, false);
        scheduledExecutorService.scheduleAtFixedRate(()->{
            try {
              ManageController.this.registerBrokerAll(true, false);
            } catch (Throwable e) {
                LOGGER.error("registerBrokerAll Exception",e);
            }
        }, 1000 * 10, 1000 * 30, TimeUnit.MILLISECONDS);
    }


  

    
    /**
     * 关闭服务
     */
    public void stop() {
      
        if (clientHousekeepingService != null) {
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
     * @date: 2018年3月14日 上午11:35:25 
     * @param checkOrderConfig
     * @param oneway
     */
    public synchronized void registerBrokerAll(final boolean checkOrderConfig, boolean oneway) {
        RegisterBrokerResult registerBrokerResult = manageOuterAPI.registerBrokerAll(
            gtsManageCfg.getManageAddr(),
            gtsManageCfg.getManageName(),
            gtsManageCfg.getManageId(),
            gtsManageCfg.getHaServerAddr(),
            oneway,
            gtsManageCfg.getRegisterBrokerTimeoutMills());
    }
    
    
    /**
     * 功能描述: 注销nameserver服务器
     * @author: chenjy
     * @date: 2018年3月14日 上午11:36:04
     */
    private void unregisterBrokerAll() {
      this.manageOuterAPI.unregisterBrokerAll(gtsManageCfg.getManageAddr(),
          gtsManageCfg.getManageName(),
          gtsManageCfg.getManageId());
    }


    
    
    
    
    
    

    public RemotingServer getRemotingServer() {
      return remotingServer;
    }



    public void setRemotingServer(RemotingServer remotingServer) {
      this.remotingServer = remotingServer;
    }



    public ScheduledExecutorService getScheduledExecutorService() {
      return scheduledExecutorService;
    }



    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
      this.scheduledExecutorService = scheduledExecutorService;
    }



    public ManageOuterAPI getManageOuterAPI() {
      return manageOuterAPI;
    }



    public void setManageOuterAPI(ManageOuterAPI manageOuterAPI) {
      this.manageOuterAPI = manageOuterAPI;
    }



    public ExecutorService getDefaultExecutor() {
      return defaultExecutor;
    }



    public void setDefaultExecutor(ExecutorService defaultExecutor) {
      this.defaultExecutor = defaultExecutor;
    }



    public BlockingQueue<Runnable> getClientManagerThreadPoolQueue() {
      return clientManagerThreadPoolQueue;
    }



    public void setClientManagerThreadPoolQueue(BlockingQueue<Runnable> clientManagerThreadPoolQueue) {
      this.clientManagerThreadPoolQueue = clientManagerThreadPoolQueue;
    }



    public ExecutorService getClientManageExecutor() {
      return clientManageExecutor;
    }



    public void setClientManageExecutor(ExecutorService clientManageExecutor) {
      this.clientManageExecutor = clientManageExecutor;
    }



    public ProducerManager getProducerManager() {
      return producerManager;
    }



    public void setProducerManager(ProducerManager producerManager) {
      this.producerManager = producerManager;
    }



    public ClientHousekeepingService getClientHousekeepingService() {
      return clientHousekeepingService;
    }



    public void setClientHousekeepingService(ClientHousekeepingService clientHousekeepingService) {
      this.clientHousekeepingService = clientHousekeepingService;
    }



    public TxManagerService getTxManagerService() {
      return txManagerService;
    }



    public void setTxManagerService(TxManagerService txManagerService) {
      this.txManagerService = txManagerService;
    }



    public TxTransactionExecutor getTxTransactionExecutor() {
      return txTransactionExecutor;
    }



    public void setTxTransactionExecutor(TxTransactionExecutor txTransactionExecutor) {
      this.txTransactionExecutor = txTransactionExecutor;
    }



    public NettyServerConfig getNettyServerConfig() {
      return nettyServerConfig;
    }



    public void setNettyServerConfig(NettyServerConfig nettyServerConfig) {
      this.nettyServerConfig = nettyServerConfig;
    }



    public NettyClientConfig getNettyClientConfig() {
      return nettyClientConfig;
    }



    public void setNettyClientConfig(NettyClientConfig nettyClientConfig) {
      this.nettyClientConfig = nettyClientConfig;
    }



    public GtsManageConfig getGtsManageCfg() {
      return gtsManageCfg;
    }



    public void setGtsManageCfg(GtsManageConfig gtsManageCfg) {
      this.gtsManageCfg = gtsManageCfg;
    }
    
}
