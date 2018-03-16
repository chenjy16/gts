package com.wf.gts.core.netty;
import com.wf.gts.core.config.TxConfig;


public interface NettyClient {

    /**
     * 启动netty客户端
     */
    void start(TxConfig txConfig);

    /**
     * 停止服务
     */
    void stop();



    /**
     * 重启
     */
    void restart();



}
