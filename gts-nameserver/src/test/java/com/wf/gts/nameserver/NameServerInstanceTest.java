package com.wf.gts.nameserver;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;

import com.wf.gts.remoting.netty.NettyServerConfig;

public class NameServerInstanceTest {
    protected NamesrvController nameSrvController = null;
    protected NettyServerConfig nettyServerConfig = new NettyServerConfig();


    @Before
    public void startup() throws Exception {
        nettyServerConfig.setListenPort(9876);
        nameSrvController = new NamesrvController(nettyServerConfig);
        //boolean initResult = nameSrvController.initialize();
        //assertThat(initResult).isTrue();
       // nameSrvController.start();
    }

    @After
    public void shutdown() throws Exception {
        if (nameSrvController != null) {
            nameSrvController.shutdown();
        }
        //maybe need to clean the file store. But we do not suggest deleting anything.
    }
}
