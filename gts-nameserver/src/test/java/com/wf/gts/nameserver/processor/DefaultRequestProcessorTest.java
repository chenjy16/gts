package com.wf.gts.nameserver.processor;
import static org.assertj.core.api.Assertions.assertThat;



import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.wf.gts.nameserver.NameSrvController;
import com.wf.gts.nameserver.route.RouteInfoManager;
import com.wf.gts.remoting.exception.RemotingCommandException;
import com.wf.gts.remoting.header.RegisterBrokerRequestHeader;
import com.wf.gts.remoting.netty.NettyServerConfig;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;
import io.netty.channel.ChannelHandlerContext;



public class DefaultRequestProcessorTest {
  
  
    private DefaultRequestProcessor defaultRequestProcessor;

    private NameSrvController namesrvController;


    private NettyServerConfig nettyServerConfig;

    private RouteInfoManager routeInfoManager;

    private Logger logger;

    @Before
    public void init() throws Exception {
        nettyServerConfig = new NettyServerConfig();
        
        routeInfoManager = new RouteInfoManager();
        
        namesrvController = new NameSrvController(nettyServerConfig);
        Field field = NameSrvController.class.getDeclaredField("routeInfoManager");
        field.setAccessible(true);
        field.set(namesrvController, routeInfoManager);
        defaultRequestProcessor = new DefaultRequestProcessor(namesrvController);

      

        logger = mock(Logger.class);
        when(logger.isInfoEnabled()).thenReturn(false);
        setFinalStatic(DefaultRequestProcessor.class.getDeclaredField("log"), logger);
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
            reg ? RequestCode.REGISTER_MANAGE : RequestCode.UNREGISTER_MANAGE, header);
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


}