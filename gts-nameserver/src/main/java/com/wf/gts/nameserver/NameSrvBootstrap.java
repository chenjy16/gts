package com.wf.gts.nameserver;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.wf.gts.nameserver.util.ShutdownHookThread;
import com.wf.gts.remoting.netty.NettyServerConfig;


@Component
public class NameSrvBootstrap{

    private static final Logger LOGGER = LoggerFactory.getLogger(NameSrvBootstrap.class);
    
    @Autowired
    NettyServerConfig nettyServerConfig;


    @PostConstruct
    public void start() throws BeansException {
      
      NamesrvController controller = new NamesrvController(nettyServerConfig); 
      try {
          Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    controller.shutdown();
                    return null;
                }
          }));
          controller.start();
        } catch (Exception e) {
            controller.shutdown();
            LOGGER.error("NameSrv服务启动异常:{}",e);
        }
        LOGGER.info("NameSrv服务成功>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");
    }
}
