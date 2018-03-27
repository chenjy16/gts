package com.wf.gts.core.bootstrap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import com.wf.gts.core.config.ClientConfig;
import com.wf.gts.core.util.SpringBeanUtils;



@Component
public class GtsTransBootstrap extends ClientConfig implements ApplicationContextAware {
  
  
    private ConfigurableApplicationContext cfgContext;
    
    /**
     * 初始化实体
     */
    @Autowired
    private  GtsTransInitialize gtsTransInitialize;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        cfgContext = (ConfigurableApplicationContext) applicationContext;
        SpringBeanUtils.getInstance().setCfgContext(cfgContext);
        start(this);
    }
    
    private void start(ClientConfig config) {
        gtsTransInitialize.init(config);
    }

    public GtsTransInitialize getGtsTransInitialize() {
      return gtsTransInitialize;
    }

    public void setGtsTransInitialize(GtsTransInitialize gtsTransInitialize) {
      this.gtsTransInitialize = gtsTransInitialize;
    }

    
}