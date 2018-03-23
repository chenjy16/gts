package com.wf.gts.nameserver;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.wf.gts.nameserver.processor.DefaultRequestProcessor;
import com.wf.gts.nameserver.route.ManageHousekeepingService;
import com.wf.gts.nameserver.route.RouteInfoManager;
import com.wf.gts.remoting.RemotingServer;
import com.wf.gts.remoting.core.ThreadFactoryImpl;
import com.wf.gts.remoting.netty.NettyRemotingServer;
import com.wf.gts.remoting.netty.NettyServerConfig;

public class NamesrvController {
  
  private final NettyServerConfig nettyServerConfig;
  private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("NSScheduledThread"));
  private RouteInfoManager routeInfoManager;
  private RemotingServer remotingServer;
  private ManageHousekeepingService manageHousekeepingService;
  private ExecutorService remotingExecutor;

  
  
  public NamesrvController( NettyServerConfig nettyServerConfig) {
      this.nettyServerConfig = nettyServerConfig;
  }

  
  public void start() throws Exception {
      initialize();
      this.remotingServer.start();
  }
  
  
  
  private void initialize() {
    this.routeInfoManager = new RouteInfoManager();
    this.manageHousekeepingService = new ManageHousekeepingService(this);
    this.remotingServer = new NettyRemotingServer(this.nettyServerConfig, this.manageHousekeepingService);
    this.remotingExecutor =Executors.newFixedThreadPool(nettyServerConfig.getServerWorkerThreads(), new ThreadFactoryImpl("RemotingExecutorThread_"));
    this.remotingServer.registerDefaultProcessor(new DefaultRequestProcessor(this), this.remotingExecutor);
    this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
            NamesrvController.this.routeInfoManager.scanNotActiveBroker();
        }
    }, 5, 10, TimeUnit.SECONDS);
  }
  
  public void shutdown() {
      this.remotingServer.shutdown();
      
      this.remotingExecutor.shutdown();
      
      this.scheduledExecutorService.shutdown();
  }



  
  
  public NettyServerConfig getNettyServerConfig() {
      return nettyServerConfig;
  }

  public RouteInfoManager getRouteInfoManager() {
      return routeInfoManager;
  }

  public RemotingServer getRemotingServer() {
      return remotingServer;
  }

  public void setRemotingServer(RemotingServer remotingServer) {
      this.remotingServer = remotingServer;
  }

}
