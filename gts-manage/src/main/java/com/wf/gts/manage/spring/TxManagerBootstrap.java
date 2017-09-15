package com.wf.gts.manage.spring;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.wf.gts.manage.netty.NettyServer;


@Component
public class TxManagerBootstrap implements ApplicationContextAware {


    private final NettyServer nettyService;

    @Autowired
    public TxManagerBootstrap(NettyServer nettyService) {
        this.nettyService = nettyService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        nettyService.start();
    }
}
