package com.wf.gts.core.bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wf.gts.core.client.GtsClient;
import com.wf.gts.core.config.ClientConfig;

/**
 * 初始化类
 */
@Component("txTransactionInitialize")
public class GtsTransInitialize {

    private static final Logger LOGGER = LoggerFactory.getLogger(GtsTransInitialize.class);
    
    private final GtsClient gtsClient;
    
    @Autowired
    public GtsTransInitialize(GtsClient gtsClient) {
        this.gtsClient = gtsClient;
    }
    
    /**
     * 功能描述: 初始化服务
     * @author: chenjy
     * @date: 2017年9月18日 下午2:57:13 
     * @param txConfig
     */
    public void init(ClientConfig config) {
      
        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            gtsClient.stop();
            LOGGER.info("系统关闭");
        }));
        
        try {
            gtsClient.start(config);
        } catch (Exception ex) {
            LOGGER.error("启动异常:{}", ex.getMessage());
            System.exit(1);//非正常关闭
        }
    }


}
