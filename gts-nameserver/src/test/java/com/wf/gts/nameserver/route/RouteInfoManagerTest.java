package com.wf.gts.nameserver.route;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.TopicConfig;
import com.wf.gts.remoting.protocol.TopicConfigSerializeWrapper;

import io.netty.channel.Channel;

public class RouteInfoManagerTest {

    private static RouteInfoManager routeInfoManager;

    @Before
    public void setup() {
        routeInfoManager = new RouteInfoManager();
    }

    @After
    public void terminate() {
        routeInfoManager.unregisterBroker("127.0.0.1:10911", "default-broker", 1234);
    }

    @Test
    public void testGetAllClusterInfo() {
        byte[] clusterInfo = routeInfoManager.getGtsManagerInfo();
        assertThat(clusterInfo).isNotNull();
    }


  

  
}