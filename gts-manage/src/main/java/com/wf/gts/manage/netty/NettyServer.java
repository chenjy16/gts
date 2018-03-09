package com.wf.gts.manage.netty;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.base.StandardSystemProperty;
import com.wf.gts.common.SocketManager;
import com.wf.gts.common.enums.SerializeProtocolEnum;
import com.wf.gts.manage.config.BrokerConfig;
import com.wf.gts.manage.domain.NettyParam;
import com.wf.gts.manage.netty.handler.NettyServerHandlerInitializer;
import com.wf.gts.manage.out.BrokerOuterAPI;
import com.wf.gts.remoting.RemotingClient;
import com.wf.gts.remoting.netty.NettyClientConfig;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.util.ThreadFactoryImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * netty服务实现
 */
@Component
public class NettyServer  {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private DefaultEventExecutorGroup servletExecutor;
    private static int MAX_THREADS = Runtime.getRuntime().availableProcessors() << 1;
    private final NettyParam nettyConfig;
    private final NettyServerHandlerInitializer nettyServerHandlerInitializer;
    
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
        "GtsScheduledThread"));
    
    private final BrokerOuterAPI brokerOuterAPI;
    private final BrokerConfig brokerConfig;

    @Autowired(required = false)
    public NettyServer(NettyParam nettyConfig, NettyServerHandlerInitializer nettyServerHandlerInitializer,BrokerConfig brokerConfig) {
        this.nettyConfig = nettyConfig;
        this.nettyServerHandlerInitializer = nettyServerHandlerInitializer;
        this.brokerConfig=brokerConfig;
        this.brokerOuterAPI=new BrokerOuterAPI(new NettyClientConfig());
    }

    
    
    public boolean initialize(){
      this.brokerOuterAPI.updateNameServerAddressList(brokerConfig.getNamesrvAddr());
      this.registerBrokerAll(true, false);
      
      this.scheduledExecutorService.scheduleAtFixedRate(()->{
          try {
            NettyServer.this.registerBrokerAll(true, false);
          } catch (Throwable e) {
              LOGGER.error("registerBrokerAll Exception", e);
          }
      }, 1000 * 10, 1000 * 30, TimeUnit.MILLISECONDS);
      return true;
    }
    
    
    
    /**
     * 启动netty服务
     */
    public void start() {
        brokerOuterAPI.start();
        initialize();
      
        SocketManager.getInstance().setMaxConnection(nettyConfig.getMaxConnection());
        servletExecutor = new DefaultEventExecutorGroup(MAX_THREADS);
        if (nettyConfig.getMaxThreads() != 0) {
            MAX_THREADS = nettyConfig.getMaxThreads();
        }
        try {
            final SerializeProtocolEnum serializeProtocolEnum =
            SerializeProtocolEnum.acquireSerializeProtocol(nettyConfig.getSerialize());
            nettyServerHandlerInitializer.setSerializeProtocolEnum(serializeProtocolEnum);
            nettyServerHandlerInitializer.setServletExecutor(servletExecutor);
            ServerBootstrap b = new ServerBootstrap();
            groups(b,MAX_THREADS<<1);
            b.bind(nettyConfig.getPort());
            LOGGER.info("netty服务启动成功,端口:{}",nettyConfig.getPort());
        } catch (Exception e) {
            LOGGER.error("netty服务启动异常:{}",e);
        }
    }


    private void groups(ServerBootstrap b, int workThreads) {
      String osName =StandardSystemProperty.OS_NAME.value();
      if (osName!=null&&osName.toLowerCase().contains("linux")&&Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(workThreads);
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class)
                    .option(EpollChannelOption.SO_BACKLOG, 100)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(EpollChannelOption.SO_KEEPALIVE, false)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyServerHandlerInitializer);
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup(workThreads);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, false)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyServerHandlerInitializer);
        }
    }

    
    /**
     * 关闭服务
     */
    public void stop() {
        unregisterBrokerAll();
        try {
            if (null != bossGroup) {
                bossGroup.shutdownGracefully().await();
            }
            if (null != workerGroup) {
                workerGroup.shutdownGracefully().await();
            }
            if (null != servletExecutor) {
                servletExecutor.shutdownGracefully().await();
            }
            if (this.brokerOuterAPI != null) {
              this.brokerOuterAPI.shutdown();
            }
        } catch (InterruptedException e) {
            LOGGER.error("netty服务关闭异常:{}",e);
        }
    }
    
    
    
    public synchronized void registerBrokerAll(final boolean checkOrderConfig, boolean oneway) {

        RegisterBrokerResult registerBrokerResult = this.brokerOuterAPI.registerBrokerAll(
            this.getBrokerAddr(),
            this.brokerConfig.getBrokerName(),
            this.brokerConfig.getBrokerId(),
            this.getHAServerAddr(),
            oneway,
            this.brokerConfig.getRegisterBrokerTimeoutMills());
    }
    
    
    
    private void unregisterBrokerAll() {
      this.brokerOuterAPI.unregisterBrokerAll(
          this.getBrokerAddr(),
          this.brokerConfig.getBrokerName(),
          this.brokerConfig.getBrokerId());
    }

    public String getBrokerAddr() {
        return this.brokerConfig.getBrokerIP1() + ":" + this.brokerConfig.getListenPort();
    }
    
    public String getHAServerAddr() {
      return this.brokerConfig.getBrokerIP2() + ":" + this.brokerConfig.getHaListenPort();
    }

    public BrokerConfig getBrokerConfig() {
      return brokerConfig;
    }
    
    
}
