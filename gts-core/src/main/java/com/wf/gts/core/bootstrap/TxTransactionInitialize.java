package com.wf.gts.core.bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wf.gts.core.config.ClientConfig;
import com.wf.gts.core.netty.NettyClient;

/**
 * 初始化类
 */
@Component("txTransactionInitialize")
public class TxTransactionInitialize {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxTransactionInitialize.class);
    
    private final NettyClient nettyClient;
    
    @Autowired
    public TxTransactionInitialize(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }
    
    /**
     * 功能描述: 初始化服务
     * @author: chenjy
     * @date: 2017年9月18日 下午2:57:13 
     * @param txConfig
     */
    public void init(ClientConfig config) {
      
        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
          nettyClient.stop();
          LOGGER.info("系统关闭");
        }));
        
        try {
            nettyClient.start(config);
        } catch (Exception ex) {
            LOGGER.error("启动异常:{}", ex.getMessage());
            System.exit(1);//非正常关闭
        }
    }


}
