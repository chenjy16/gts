package com.wf.gts.core.netty.impl;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.StandardSystemProperty;
import com.wf.gts.common.enums.SerializeProtocolEnum;
import com.wf.gts.core.client.MQClientInstance;
import com.wf.gts.core.config.TxConfig;
import com.wf.gts.core.netty.NettyClient;
import com.wf.gts.core.netty.handler.NettyClientHandlerInitializer;
import com.wf.gts.remoting.protocol.BrokerLiveInfo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;


@Service
public class NettyClientImpl implements NettyClient {
   
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientImpl.class);
    private TxConfig txConfig;
    private EventLoopGroup workerGroup;
    private DefaultEventExecutorGroup servletExecutor;
    private String host = "127.0.0.1";
    private Integer port = 8888;
    private Channel channel;
    private Bootstrap bootstrap;
    private final NettyClientHandlerInitializer nettyClientHandlerInitializer;
    MQClientInstance  clientInstance;
    
    @Autowired
    public NettyClientImpl(NettyClientHandlerInitializer nettyClientHandlerInitializer) {
        this.nettyClientHandlerInitializer = nettyClientHandlerInitializer;
    }

    /**
     * 启动netty客户端
     */
    @Override
    public void start(TxConfig txConfig) {
      
        this.clientInstance = new MQClientInstance(txConfig, null);
        this.clientInstance.start();
        this.txConfig = txConfig;
        SerializeProtocolEnum serializeProtocol =SerializeProtocolEnum.acquireSerializeProtocol(txConfig.getNettySerializer());
        nettyClientHandlerInitializer.setSerializeProtocolEnum(serializeProtocol);
        servletExecutor = new DefaultEventExecutorGroup(txConfig.getNettyThreadMax());
        nettyClientHandlerInitializer.setServletExecutor(servletExecutor);
        nettyClientHandlerInitializer.setTxConfig(txConfig);
        try {
            bootstrap = new Bootstrap();
            groups(bootstrap, txConfig.getNettyThreadMax());
            doConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void groups(Bootstrap bootstrap, int workThreads) {
        String osName =StandardSystemProperty.OS_NAME.value();
        if (osName!=null&&osName.toLowerCase().contains("linux")&&Epoll.isAvailable()) {
            workerGroup = new EpollEventLoopGroup(workThreads);
            bootstrap.group(workerGroup);
            bootstrap.channel(EpollSocketChannel.class);
            bootstrap
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(EpollChannelOption.SO_KEEPALIVE, false)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(nettyClientHandlerInitializer);
        } else {
            workerGroup = new NioEventLoopGroup(workThreads);
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, false)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(nettyClientHandlerInitializer);
        }
    }


    public void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        //从nameserver获取manager地址
        AtomicReference<BrokerLiveInfo> brokerAddrTable=clientInstance.getBrokerAddrTable();
        String brokerAddr=brokerAddrTable.get().getBrokerAddr();
        String[] s = brokerAddr.split(":");
        ChannelFuture future = bootstrap.connect(s[0], Integer.parseInt(s[1]));
        LOGGER.info("连接txManager-socket服务-> host:port:{}",host + ":" + port);
        future.addListener((ChannelFutureListener) futureListener -> {
            if (futureListener.isSuccess()) {
                channel = futureListener.channel();
                LOGGER.info("Connect to server successfully!-> host:port:{}", host + ":" + port);
            } else {
                LOGGER.info("Failed to connect to server, try connect after 5s-> host:port:{}",host + ":" + port);
                futureListener.channel().eventLoop().schedule(this::doConnect, 5, TimeUnit.SECONDS);
            }
            
        });
    }

    
    /**
     * 停止服务
     */
    @Override
    public void stop() {
        if (Objects.nonNull(servletExecutor)) {
            workerGroup.shutdownGracefully();
        }
        if (Objects.nonNull(servletExecutor)) {
            servletExecutor.shutdownGracefully();
        }
        this.clientInstance.shutdown();
    }

    /**
     * 重启
     */
    @Override
    public void restart() {
        stop();
        start(txConfig);
    }


}
