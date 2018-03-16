package com.wf.gts.nameserver.processor;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.wf.gts.nameserver.NamesrvConfig;
import com.wf.gts.nameserver.NamesrvController;
import com.wf.gts.nameserver.route.RouteInfoManager;
import com.wf.gts.remoting.exception.RemotingCommandException;
import com.wf.gts.remoting.header.RegisterBrokerRequestHeader;
import com.wf.gts.remoting.netty.NettyServerConfig;
import com.wf.gts.remoting.protocol.BrokerData;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;
import com.wf.gts.remoting.protocol.TopicConfig;
import com.wf.gts.remoting.protocol.TopicConfigSerializeWrapper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;



public class DefaultRequestProcessorTest {
  
  
    private DefaultRequestProcessor defaultRequestProcessor;

    private NamesrvController namesrvController;

    private NamesrvConfig namesrvConfig;

    private NettyServerConfig nettyServerConfig;

    private RouteInfoManager routeInfoManager;

    private Logger logger;

    @Before
    public void init() throws Exception {
        namesrvConfig = new NamesrvConfig();
        nettyServerConfig = new NettyServerConfig();
        
        routeInfoManager = new RouteInfoManager();
        
        namesrvController = new NamesrvController(namesrvConfig, nettyServerConfig);
        Field field = NamesrvController.class.getDeclaredField("routeInfoManager");
        field.setAccessible(true);
        field.set(namesrvController, routeInfoManager);
        defaultRequestProcessor = new DefaultRequestProcessor(namesrvController);

        registerRouteInfoManager();

        logger = mock(Logger.class);
        when(logger.isInfoEnabled()).thenReturn(false);
        setFinalStatic(DefaultRequestProcessor.class.getDeclaredField("log"), logger);
    }
    
    
    
    

   


    @Test
    public void testProcessRequest_RegisterBroker() throws RemotingCommandException,
        NoSuchFieldException, IllegalAccessException {
        RemotingCommand request = genSampleRegisterCmd(true);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        when(ctx.channel()).thenReturn(null);

        RemotingCommand response = defaultRequestProcessor.processRequest(ctx, request);

        assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS);
        assertThat(response.getRemark()).isNull();

        RouteInfoManager routes = namesrvController.getRouteInfoManager();
        Field brokerAddrTable = RouteInfoManager.class.getDeclaredField("brokerAddrTable");
        brokerAddrTable.setAccessible(true);

        BrokerData broker = new BrokerData();
        broker.setBrokerName("broker");
        broker.setBrokerAddrs((HashMap) Maps.newHashMap(new Long(2333), "10.10.1.1"));

        assertThat((Map) brokerAddrTable.get(routes))
            .contains(new HashMap.SimpleEntry("broker", broker));
    }

    @Test
    public void testProcessRequest_RegisterBrokerWithFilterServer() throws RemotingCommandException,
        NoSuchFieldException, IllegalAccessException {
        RemotingCommand request = genSampleRegisterCmd(true);

        // version >= MQVersion.Version.V3_0_11.ordinal() to register with filter server
        request.setVersion(100);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        when(ctx.channel()).thenReturn(null);

        RemotingCommand response = defaultRequestProcessor.processRequest(ctx, request);

        assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS);
        assertThat(response.getRemark()).isNull();

        RouteInfoManager routes = namesrvController.getRouteInfoManager();
        Field brokerAddrTable = RouteInfoManager.class.getDeclaredField("brokerAddrTable");
        brokerAddrTable.setAccessible(true);

        BrokerData broker = new BrokerData();
        broker.setBrokerName("broker");
        broker.setBrokerAddrs((HashMap) Maps.newHashMap(new Long(2333), "10.10.1.1"));

        assertThat((Map) brokerAddrTable.get(routes))
            .contains(new HashMap.SimpleEntry("broker", broker));
    }

    @Test
    public void testProcessRequest_UnregisterBroker() throws RemotingCommandException, NoSuchFieldException, IllegalAccessException {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        when(ctx.channel()).thenReturn(null);

        //Register broker
        RemotingCommand regRequest = genSampleRegisterCmd(true);
        defaultRequestProcessor.processRequest(ctx, regRequest);

        //Unregister broker
        RemotingCommand unregRequest = genSampleRegisterCmd(false);
        RemotingCommand unregResponse = defaultRequestProcessor.processRequest(ctx, unregRequest);

        assertThat(unregResponse.getCode()).isEqualTo(ResponseCode.SUCCESS);
        assertThat(unregResponse.getRemark()).isNull();

        RouteInfoManager routes = namesrvController.getRouteInfoManager();
        Field brokerAddrTable = RouteInfoManager.class.getDeclaredField("brokerAddrTable");
        brokerAddrTable.setAccessible(true);

        assertThat((Map) brokerAddrTable.get(routes)).isNotEmpty();
    }

    private static RemotingCommand genSampleRegisterCmd(boolean reg) {
        RegisterBrokerRequestHeader header = new RegisterBrokerRequestHeader();
        header.setBrokerName("broker");
        RemotingCommand request = RemotingCommand.createRequestCommand(
            reg ? RequestCode.REGISTER_BROKER : RequestCode.UNREGISTER_BROKER, header);
        request.addExtField("brokerName", "broker");
        request.addExtField("brokerAddr", "10.10.1.1");
        request.addExtField("clusterName", "cluster");
        request.addExtField("haServerAddr", "10.10.2.1");
        request.addExtField("brokerId", "2333");
        return request;
    }

    private static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    private void registerRouteInfoManager() {
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

    }
}