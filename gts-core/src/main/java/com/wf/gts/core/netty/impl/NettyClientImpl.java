package com.wf.gts.core.netty.impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.wf.gts.core.client.MQClientInstance;
import com.wf.gts.core.client.MQClientManager;
import com.wf.gts.core.config.TxConfig;
import com.wf.gts.core.netty.NettyClient;



@Service
public class NettyClientImpl implements NettyClient {
   
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientImpl.class);
    private TxConfig txConfig;
    MQClientInstance  clientInstance;
     

    /**
     * 启动netty客户端
     */
    @Override
    public void start(TxConfig txConfig) {
      
        this.clientInstance = MQClientManager.getInstance()
            .getAndCreateMQClientInstance(null);
        this.clientInstance.start();
        this.txConfig = txConfig;
        
    }
    
    /**
     * 停止服务
     */
    @Override
    public void stop() {
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
