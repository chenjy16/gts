package com.wf.gts.core.netty;
import com.wf.gts.core.config.ClientConfig;


public interface NettyClient {

    /**
     * 启动netty客户端
     */
    void start(ClientConfig txConfig);

    /**
     * 停止服务
     */
    void stop();



}
