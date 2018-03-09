package com.wf.gts.nameserver.route;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.TopicConfig;
import com.wf.gts.remoting.protocol.TopicConfigSerializeWrapper;
import com.wf.gts.remoting.protocol.TopicRouteData;

import io.netty.channel.Channel;

public class RouteInfoManagerTest {

    private static RouteInfoManager routeInfoManager;

    @Before
    public void setup() {
        routeInfoManager = new RouteInfoManager();
        testRegisterBroker();
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


    @Test
    public void testRegisterBroker() {
        TopicConfigSerializeWrapper topicConfigSerializeWrapper = new TopicConfigSerializeWrapper();
        ConcurrentHashMap<String, TopicConfig> topicConfigConcurrentHashMap = new ConcurrentHashMap<>();
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setWriteQueueNums(8);
        topicConfig.setTopicName("unit-test");
        topicConfig.setPerm(6);
        topicConfig.setReadQueueNums(8);
        topicConfig.setOrder(false);
        topicConfigConcurrentHashMap.put("unit-test", topicConfig);
        topicConfigSerializeWrapper.setTopicConfigTable(topicConfigConcurrentHashMap);
        Channel channel = mock(Channel.class);
        RegisterBrokerResult registerBrokerResult = routeInfoManager.registerBroker("127.0.0.1:10911", "default-broker", 1234, "127.0.0.1:1001",
            topicConfigSerializeWrapper, channel);
        assertThat(registerBrokerResult).isNotNull();
    }

 


 
  

  

  
}