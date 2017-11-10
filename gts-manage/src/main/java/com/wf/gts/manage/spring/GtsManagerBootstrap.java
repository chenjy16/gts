package com.wf.gts.manage.spring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.wf.gts.manage.netty.NettyServer;


@Component
public class GtsManagerBootstrap implements ApplicationContextAware {

  private static final Logger LOGGER = LoggerFactory.getLogger(GtsManagerBootstrap.class);
  
    private final NettyServer nettyService;

    @Autowired
    public GtsManagerBootstrap(NettyServer nettyService) {
        this.nettyService = nettyService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
          nettyService.start();
        } catch (Exception e) {
          LOGGER.error("全局事务管理服务启动异常:{}",e);
        }
        LOGGER.info("全局事务管理服务成功>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");
    }
}
