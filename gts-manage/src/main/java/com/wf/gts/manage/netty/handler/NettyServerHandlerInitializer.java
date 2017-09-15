package com.wf.gts.manage.netty.handler;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wf.gts.common.enums.SerializeProtocolEnum;
import com.wf.gts.manage.domain.NettyParam;
import com.wf.gts.manage.netty.NettyPipelineInit;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * netty服务初始化
 */
@Component
public class NettyServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyParam nettyConfig;
    private final NettyServerMessageHandler nettyServerMessageHandler;
    private SerializeProtocolEnum serializeProtocolEnum;
    private DefaultEventExecutorGroup servletExecutor;

    public void setServletExecutor(DefaultEventExecutorGroup servletExecutor) {
        this.servletExecutor = servletExecutor;
    }

    @Autowired
    public NettyServerHandlerInitializer(NettyParam nettyConfig, NettyServerMessageHandler nettyServerMessageHandler) {
        this.nettyConfig = nettyConfig;
        this.nettyServerMessageHandler = nettyServerMessageHandler;
    }

    public void setSerializeProtocolEnum(SerializeProtocolEnum serializeProtocolEnum) {
        this.serializeProtocolEnum = serializeProtocolEnum;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        NettyPipelineInit.serializePipeline(serializeProtocolEnum,pipeline);
        pipeline.addLast("timeout",
                new IdleStateHandler(
                    nettyConfig.getHeartTime(), 
                    nettyConfig.getHeartTime(),
                    nettyConfig.getHeartTime(), 
                    TimeUnit.SECONDS));
        pipeline.addLast(nettyServerMessageHandler);
    }
}
