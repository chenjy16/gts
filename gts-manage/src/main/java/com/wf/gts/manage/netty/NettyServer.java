package com.wf.gts.manage.netty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.wf.gts.common.SocketManager;
import com.wf.gts.common.enums.SerializeProtocolEnum;
import com.wf.gts.manage.domain.NettyParam;
import com.wf.gts.manage.netty.handler.NettyServerHandlerInitializer;
import com.wf.gts.manage.service.TxManagerService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
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
    private final TxManagerService txManagerService;
    private final NettyParam nettyConfig;
    private final NettyServerHandlerInitializer nettyServerHandlerInitializer;

    @Autowired(required = false)
    public NettyServer(TxManagerService txManagerService, NettyParam nettyConfig, NettyServerHandlerInitializer nettyServerHandlerInitializer) {
        this.txManagerService = txManagerService;
        this.nettyConfig = nettyConfig;
        this.nettyServerHandlerInitializer = nettyServerHandlerInitializer;
    }

    
    
    /**
     * 启动netty服务
     */
    public void start() {
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
  /*      if (Objects.equals(StandardSystemProperty.OS_NAME.value(), "Linux")) {
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(workThreads);
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class)
                    .option(EpollChannelOption.TCP_CORK, true)
                    .option(EpollChannelOption.SO_KEEPALIVE, true)
                    .option(EpollChannelOption.SO_BACKLOG, 100)
                    .option(EpollChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(EpollChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyServerHandlerInitializer);
        } else {*/
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup(workThreads);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyServerHandlerInitializer);
       // }
    }

    
    /**
     * 关闭服务
     */
    public void stop() {
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
        } catch (InterruptedException e) {
            LOGGER.info("netty服务关闭异常:{}",e);
        }
    }
}
