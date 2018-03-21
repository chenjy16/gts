package com.wf.gts.core.netty.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wf.gts.core.client.ClientInstance;
import com.wf.gts.core.config.ClientConfig;
import com.wf.gts.core.netty.NettyClient;



@Service
public class NettyClientImpl implements NettyClient {
  
    
    @Autowired
    private ClientInstance  clientInstance;
     

    /**
     * 启动netty客户端
     */
    @Override
    public void start(ClientConfig config) {
        this.clientInstance.start(config);
    }
    
    /**
     * 停止服务
     */
    @Override
    public void stop() {
        this.clientInstance.shutdown();
    }

   

}
