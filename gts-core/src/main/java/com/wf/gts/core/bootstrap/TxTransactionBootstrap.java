package com.wf.gts.core.bootstrap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import com.wf.gts.core.config.ClientConfig;
import com.wf.gts.core.util.SpringBeanUtils;


/**
 * TxTransaction 启动类
 */
@Component
public class TxTransactionBootstrap extends ClientConfig implements ApplicationContextAware {
  
  
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
    
    private void start(ClientConfig config) {
        txTransactionInitialize.init(config);
    }

    
    public TxTransactionInitialize getTxTransactionInitialize() {
      return txTransactionInitialize;
    }


    public void setTxTransactionInitialize(TxTransactionInitialize txTransactionInitialize) {
      this.txTransactionInitialize = txTransactionInitialize;
    }
    
    
}