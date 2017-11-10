package com.wf.gts.core.bean.bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wf.gts.core.config.TxConfig;
import com.wf.gts.core.service.InitService;

/**
 * 初始化类
 */
@Component
public class TxTransactionInitialize {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxTransactionInitialize.class);
    private final InitService initService;
    
    
    
    @Autowired
    public TxTransactionInitialize(InitService initService) {
        this.initService = initService;
    }
    
    /**
     * 功能描述: 初始化服务
     * @author: chenjy
     * @date: 2017年9月18日 下午2:57:13 
     * @param txConfig
     */
    public void init(TxConfig txConfig) {
      
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.error("系统关闭")));
        try {
            initService.initialization(txConfig);
        } catch (Exception ex) {
            LOGGER.error("初始化异常:{}", ex.getMessage());
            System.exit(1);//非正常关闭
        }
    }


}
