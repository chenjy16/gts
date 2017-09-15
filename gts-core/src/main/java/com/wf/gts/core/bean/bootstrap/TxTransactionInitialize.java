package com.wf.gts.core.bean.bootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wf.gts.core.config.TxConfig;
import com.wf.gts.core.service.InitService;

/**
 * 初始化类
 */
@Component
public class TxTransactionInitialize {

  

    private final InitService initService;

    @Autowired
    public TxTransactionInitialize(InitService initService) {
        this.initService = initService;
    }

    /**
     * 初始化服务
     */
    public void init(TxConfig txConfig) {
       // Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.error("系统关闭")));
        try {
            initService.initialization(txConfig);
        } catch (RuntimeException ex) {
            //LogUtil.error(LOGGER, "初始化异常:{}", ex::getMessage);
            System.exit(1);//非正常关闭
        }
    }


}
