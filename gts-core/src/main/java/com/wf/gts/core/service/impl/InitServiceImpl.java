package com.wf.gts.core.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wf.gts.core.config.TxConfig;
import com.wf.gts.core.netty.NettyClient;
import com.wf.gts.core.service.InitService;

@Component
public class InitServiceImpl implements InitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitServiceImpl.class);

    private final NettyClient nettyClient;

    
    @Autowired
    public InitServiceImpl(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    
    @Override
    public void initialization(TxConfig txConfig) {
        try {
            nettyClient.start(txConfig);
        } catch (Exception e) {
            throw e;
        }
        LOGGER.info("启动NettyClient");
    }

}
