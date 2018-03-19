package com.wf.gts.manage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.wf.gts.manage.ManageController;


@Component
public class GtsManagerBootstrap implements ApplicationContextAware {

  private static final Logger LOGGER = LoggerFactory.getLogger(GtsManagerBootstrap.class);
  
  
    @Autowired
    private  ManageController manageController;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
          manageController.start();
        } catch (Exception e) {
          LOGGER.error("全局事务管理服务启动异常:{}",e);
        }
        LOGGER.info("全局事务管理服务成功>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");
    }
}
