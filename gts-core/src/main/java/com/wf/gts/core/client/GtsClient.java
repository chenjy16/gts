package com.wf.gts.core.client;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import com.wf.gts.core.client.ClientInstance;
import com.wf.gts.core.config.ClientConfig;



@Service
public class GtsClient  {
  
    
    @Autowired
    private ClientInstance  clientInstance;
     

    /**
     * 启动netty客户端
     */
    public void start(ClientConfig config) {
        this.clientInstance.start(config);
    }
    
    /**
     * 停止服务
     */
    public void stop() {
        this.clientInstance.shutdown();
    }

   

}
