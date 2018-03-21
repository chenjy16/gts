package com.wf.gts.nameserver;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.nameserver.processor.DefaultRequestProcessor;
import com.wf.gts.nameserver.route.ManageHousekeepingService;
import com.wf.gts.nameserver.route.RouteInfoManager;
import com.wf.gts.remoting.RemotingServer;
import com.wf.gts.remoting.core.ThreadFactoryImpl;
import com.wf.gts.remoting.netty.NettyRemotingServer;
import com.wf.gts.remoting.netty.NettyServerConfig;


public class NamesrvController {
  
  private static final Logger log = LoggerFactory.getLogger(NamesrvController.class);
  private final NamesrvConfig namesrvConfig;
  private final NettyServerConfig nettyServerConfig;
  private final ScheduledExecutorService scheduledExecutorService = 
      Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
      "NSScheduledThread"));
  
  private final RouteInfoManager routeInfoManager;
  private RemotingServer remotingServer;
  private ManageHousekeepingService brokerHousekeepingService;
  private ExecutorService remotingExecutor;

  
  
  public NamesrvController(NamesrvConfig namesrvConfig, NettyServerConfig nettyServerConfig) {
      this.namesrvConfig = namesrvConfig;
      this.nettyServerConfig = nettyServerConfig;
      this.routeInfoManager = new RouteInfoManager();
      this.brokerHousekeepingService = new ManageHousekeepingService(this);
  }

  
  
  public boolean initialize() {

      this.remotingServer = new NettyRemotingServer(this.nettyServerConfig, this.brokerHousekeepingService);

      this.remotingExecutor =
          Executors.newFixedThreadPool(nettyServerConfig.getServerWorkerThreads(), new ThreadFactoryImpl("RemotingExecutorThread_"));

      this.registerProcessor();

      
      this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

          @Override
          public void run() {
              NamesrvController.this.routeInfoManager.scanNotActiveBroker();
          }
      }, 5, 10, TimeUnit.SECONDS);


      return true;
  }

  private void registerProcessor() {
      this.remotingServer.registerDefaultProcessor(new DefaultRequestProcessor(this), this.remotingExecutor);
  }

  
  
  
  public void start() throws Exception {
      this.remotingServer.start();
  }

  public void shutdown() {
      this.remotingServer.shutdown();
      
      this.remotingExecutor.shutdown();
      
      this.scheduledExecutorService.shutdown();
  }

  public NamesrvConfig getNamesrvConfig() {
      return namesrvConfig;
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
