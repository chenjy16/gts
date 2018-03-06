package com.wf.gts.nameserver.processor;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.nameserver.NamesrvController;
import com.wf.gts.nameserver.header.GetRouteInfoRequestHeader;
import com.wf.gts.nameserver.header.RegisterBrokerRequestHeader;
import com.wf.gts.nameserver.header.RegisterBrokerResponseHeader;
import com.wf.gts.nameserver.header.UnRegisterBrokerRequestHeader;
import com.wf.gts.nameserver.util.MQVersion;
import com.wf.gts.nameserver.util.MQVersion.Version;
import com.wf.gts.remoting.exception.RemotingCommandException;
import com.wf.gts.remoting.netty.NettyRequestProcessor;
import com.wf.gts.remoting.protocol.RegisterBrokerBody;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;
import com.wf.gts.remoting.protocol.TopicConfigSerializeWrapper;
import com.wf.gts.remoting.protocol.TopicRouteData;
import com.wf.gts.remoting.util.MixAll;
import com.wf.gts.remoting.util.RemotingHelper;
import io.netty.channel.ChannelHandlerContext;



public class DefaultRequestProcessor implements NettyRequestProcessor {
  
    private static final Logger log = LoggerFactory.getLogger(DefaultRequestProcessor.class);
    protected final NamesrvController namesrvController;

    public DefaultRequestProcessor(NamesrvController namesrvController) {
        this.namesrvController = namesrvController;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx,
        RemotingCommand request) throws RemotingCommandException {
      
        if (log.isDebugEnabled()) {
            log.debug("receive request, {} {} {}",
                request.getCode(),
                RemotingHelper.parseChannelRemoteAddr(ctx.channel()),
                request);
        }
        switch (request.getCode()) {
            case RequestCode.REGISTER_BROKER:
                Version brokerVersion = MQVersion.value2Version(request.getVersion());
                if (brokerVersion.ordinal() >= MQVersion.Version.V3_0_11.ordinal()) {
                    return this.registerBrokerWithFilterServer(ctx, request);
                } else {
                    return this.registerBroker(ctx, request);
                }
            case RequestCode.UNREGISTER_BROKER:
                return this.unregisterBroker(ctx, request);         
            case RequestCode.GET_ROUTEINTO_BY_TOPIC:
                return this.getRouteInfoByTopic(ctx, request);
            case RequestCode.UPDATE_NAMESRV_CONFIG:
                return this.updateConfig(ctx, request);
            case RequestCode.GET_NAMESRV_CONFIG:
                return this.getConfig(ctx, request);
            default:
                break;
        }
        return null;
    }
    
    @Override
    public boolean rejectRequest() {
        return false;
    }

   

    public RemotingCommand registerBrokerWithFilterServer(ChannelHandlerContext ctx, RemotingCommand request)
        throws RemotingCommandException {
        final RemotingCommand response = RemotingCommand.createResponseCommand(RegisterBrokerResponseHeader.class);
        final RegisterBrokerResponseHeader responseHeader = (RegisterBrokerResponseHeader) response.readCustomHeader();
        final RegisterBrokerRequestHeader requestHeader =
            (RegisterBrokerRequestHeader) request.decodeCommandCustomHeader(RegisterBrokerRequestHeader.class);

        RegisterBrokerBody registerBrokerBody = new RegisterBrokerBody();

        if (request.getBody() != null) {
            registerBrokerBody = RegisterBrokerBody.decode(request.getBody(), RegisterBrokerBody.class);
        } else {
            registerBrokerBody.getTopicConfigSerializeWrapper().getDataVersion().setCounter(new AtomicLong(0));
            registerBrokerBody.getTopicConfigSerializeWrapper().getDataVersion().setTimestamp(0);
        }

        RegisterBrokerResult result = this.namesrvController.getRouteInfoManager().registerBroker(
            requestHeader.getClusterName(),
            requestHeader.getBrokerAddr(),
            requestHeader.getBrokerName(),
            requestHeader.getBrokerId(),
            requestHeader.getHaServerAddr(),
            registerBrokerBody.getTopicConfigSerializeWrapper(),
            registerBrokerBody.getFilterServerList(),
            ctx.channel());
        responseHeader.setHaServerAddr(result.getHaServerAddr());
        responseHeader.setMasterAddr(result.getMasterAddr());
        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

    
    
    public RemotingCommand registerBroker(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
        final RemotingCommand response = RemotingCommand.createResponseCommand(RegisterBrokerResponseHeader.class);
        final RegisterBrokerResponseHeader responseHeader = (RegisterBrokerResponseHeader) response.readCustomHeader();
        final RegisterBrokerRequestHeader requestHeader =
            (RegisterBrokerRequestHeader) request.decodeCommandCustomHeader(RegisterBrokerRequestHeader.class);

        TopicConfigSerializeWrapper topicConfigWrapper;
        if (request.getBody() != null) {
            topicConfigWrapper = TopicConfigSerializeWrapper.decode(request.getBody(), TopicConfigSerializeWrapper.class);
        } else {
            topicConfigWrapper = new TopicConfigSerializeWrapper();
            topicConfigWrapper.getDataVersion().setCounter(new AtomicLong(0));
            topicConfigWrapper.getDataVersion().setTimestamp(0);
        }

        RegisterBrokerResult result = this.namesrvController.getRouteInfoManager().registerBroker(
            requestHeader.getClusterName(),
            requestHeader.getBrokerAddr(),
            requestHeader.getBrokerName(),
            requestHeader.getBrokerId(),
            requestHeader.getHaServerAddr(),
            topicConfigWrapper,
            null,
            ctx.channel()
        );

        responseHeader.setHaServerAddr(result.getHaServerAddr());
        responseHeader.setMasterAddr(result.getMasterAddr());

       // byte[] jsonValue = this.namesrvController.getKvConfigManager().getKVListByNamespace("ORDER_TOPIC_CONFIG");
       // response.setBody(jsonValue);
        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

    
    
    
    
    
    public RemotingCommand unregisterBroker(ChannelHandlerContext ctx,
        RemotingCommand request) throws RemotingCommandException {
        final RemotingCommand response = RemotingCommand.createResponseCommand(null);
        final UnRegisterBrokerRequestHeader requestHeader =
            (UnRegisterBrokerRequestHeader) request.decodeCommandCustomHeader(UnRegisterBrokerRequestHeader.class);

        this.namesrvController.getRouteInfoManager().unregisterBroker(
            requestHeader.getClusterName(),
            requestHeader.getBrokerAddr(),
            requestHeader.getBrokerName(),
            requestHeader.getBrokerId());

        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

    public RemotingCommand getRouteInfoByTopic(ChannelHandlerContext ctx,
        RemotingCommand request) throws RemotingCommandException {
      
        final RemotingCommand response = RemotingCommand.createResponseCommand(null);
        
        final GetRouteInfoRequestHeader requestHeader =
            (GetRouteInfoRequestHeader) request.decodeCommandCustomHeader(GetRouteInfoRequestHeader.class);

        TopicRouteData topicRouteData = this.namesrvController.getRouteInfoManager().pickupTopicRouteData(requestHeader.getTopic());

        if (topicRouteData != null) {
           
            byte[] content = topicRouteData.encode();
            response.setBody(content);
            response.setCode(ResponseCode.SUCCESS);
            response.setRemark(null);
            return response;
        }

        response.setCode(ResponseCode.TOPIC_NOT_EXIST);
        response.setRemark("No topic route info in name server for the topic: " + requestHeader.getTopic());
        return response;
    }
    

    private RemotingCommand updateConfig(ChannelHandlerContext ctx, RemotingCommand request) {
        log.info("updateConfig called by {}", RemotingHelper.parseChannelRemoteAddr(ctx.channel()));

        final RemotingCommand response = RemotingCommand.createResponseCommand(null);

        byte[] body = request.getBody();
        if (body != null) {
            String bodyStr;
            try {
                bodyStr = new String(body, MixAll.DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                log.error("updateConfig byte array to string error: ", e);
                response.setCode(ResponseCode.SYSTEM_ERROR);
                response.setRemark("UnsupportedEncodingException " + e);
                return response;
            }

            if (bodyStr == null) {
                log.error("updateConfig get null body!");
                response.setCode(ResponseCode.SYSTEM_ERROR);
                response.setRemark("string2Properties error");
                return response;
            }

            Properties properties = MixAll.string2Properties(bodyStr);
            if (properties == null) {
                log.error("updateConfig MixAll.string2Properties error {}", bodyStr);
                response.setCode(ResponseCode.SYSTEM_ERROR);
                response.setRemark("string2Properties error");
                return response;
            }

            this.namesrvController.getConfiguration().update(properties);
        }

        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

    private RemotingCommand getConfig(ChannelHandlerContext ctx, RemotingCommand request) {
        final RemotingCommand response = RemotingCommand.createResponseCommand(null);

        String content = this.namesrvController.getConfiguration().getAllConfigsFormatString();
        if (content != null && content.length() > 0) {
            try {
                response.setBody(content.getBytes(MixAll.DEFAULT_CHARSET));
            } catch (UnsupportedEncodingException e) {
                log.error("getConfig error, ", e);
                response.setCode(ResponseCode.SYSTEM_ERROR);
                response.setRemark("UnsupportedEncodingException " + e);
                return response;
            }
        }

        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

}
