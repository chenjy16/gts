package com.wf.gts.core.bean.bootstrap;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import com.wf.gts.core.config.TxConfig;
import com.wf.gts.core.util.SpringBeanUtils;


/**
 * TxTransaction 启动类
 */
@Component
public class TxTransactionBootstrap extends TxConfig implements ApplicationContextAware {
  
    private ConfigurableApplicationContext cfgContext;
    /**
     * 初始化实体
     */
    @Autowired
    private  TxTransactionInitialize txTransactionInitialize;

    

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        cfgContext = (ConfigurableApplicationContext) applicationContext;
        SpringBeanUtils.getInstance().setCfgContext(cfgContext);
        start(this);
    }
    
    private void start(TxConfig txConfig) {
        if (!checkDataConfig(txConfig)) {
            throw new RuntimeException("分布式事务配置信息不完整！");
        }
        txTransactionInitialize.init(txConfig);
    }

    
    private boolean checkDataConfig(TxConfig txConfig) {
        return !StringUtils.isBlank(txConfig.getTxManagerUrl());
    }


    public TxTransactionInitialize getTxTransactionInitialize() {
      return txTransactionInitialize;
    }


    public void setTxTransactionInitialize(TxTransactionInitialize txTransactionInitialize) {
      this.txTransactionInitialize = txTransactionInitialize;
    }
    
    
}