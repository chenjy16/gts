package com.wf.gts.nameserver.processor;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.nameserver.NamesrvController;
import com.wf.gts.remoting.exception.RemotingCommandException;
import com.wf.gts.remoting.header.RegisterBrokerRequestHeader;
import com.wf.gts.remoting.header.RegisterBrokerResponseHeader;
import com.wf.gts.remoting.header.UnRegisterBrokerRequestHeader;
import com.wf.gts.remoting.netty.NettyRequestProcessor;
import com.wf.gts.remoting.protocol.RegisterBrokerBody;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;
import com.wf.gts.remoting.util.RemotingHelper;
import io.netty.channel.ChannelHandlerContext;


public class DefaultRequestProcessor implements NettyRequestProcessor {
  
    private static final Logger log = LoggerFactory.getLogger(DefaultRequestProcessor.class);
    protected final NamesrvController namesrvController;

    public DefaultRequestProcessor(NamesrvController namesrvController) {
        this.namesrvController = namesrvController;
    }

    
    /**
     * 请求分发
     */
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
      
        if (log.isDebugEnabled()) {
            log.debug("receive request, {} {} {}",
                request.getCode(),
                RemotingHelper.parseChannelRemoteAddr(ctx.channel()),
                request);
        }
        switch (request.getCode()) {
            case RequestCode.REGISTER_BROKER:
                return this.registerBrokerWithFilterServer(ctx, request);
            case RequestCode.UNREGISTER_BROKER:
                return this.unregisterBroker(ctx, request); 
            case RequestCode.GET_BROKER_CLUSTER_INFO:
              return this.getBrokerClusterInfo(ctx, request);
            default:
                break;
        }
        return null;
    }
    
    @Override
    public boolean rejectRequest() {
        return false;
    }

   
    /**
     * 功能描述: 注册
     * @author: chenjy
     * @date: 2018年3月7日 下午4:01:39 
     * @param ctx
     * @param request
     * @return
     * @throws RemotingCommandException
     */
    public RemotingCommand registerBrokerWithFilterServer(ChannelHandlerContext ctx, RemotingCommand request)throws RemotingCommandException {
       
        final RemotingCommand response = RemotingCommand.createResponseCommand(RegisterBrokerResponseHeader.class);
        final RegisterBrokerResponseHeader responseHeader = (RegisterBrokerResponseHeader) response.readCustomHeader();
        final RegisterBrokerRequestHeader requestHeader =(RegisterBrokerRequestHeader) request.decodeCommandCustomHeader(RegisterBrokerRequestHeader.class);
        RegisterBrokerBody registerBrokerBody = new RegisterBrokerBody();
        if (request.getBody() != null) {
            registerBrokerBody = RegisterBrokerBody.decode(request.getBody(), RegisterBrokerBody.class);
        } else {
            registerBrokerBody.getTopicConfigSerializeWrapper().getDataVersion().setCounter(new AtomicLong(0));
            registerBrokerBody.getTopicConfigSerializeWrapper().getDataVersion().setTimestamp(0);
        }

        RegisterBrokerResult result = this.namesrvController.getRouteInfoManager().registerBroker(
            requestHeader.getBrokerAddr(),
            requestHeader.getBrokerName(),
            requestHeader.getBrokerId(),
            requestHeader.getHaServerAddr(),
            registerBrokerBody.getTopicConfigSerializeWrapper(),
            ctx.channel());
        responseHeader.setHaServerAddr(result.getHaServerAddr());
        responseHeader.setMasterAddr(result.getMasterAddr());
        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

    
    
    /**
     * 功能描述: 注销
     * @author: chenjy
     * @date: 2018年3月7日 下午4:02:01 
     * @param ctx
     * @param request
     * @return
     * @throws RemotingCommandException
     */
    public RemotingCommand unregisterBroker(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
        final RemotingCommand response = RemotingCommand.createResponseCommand(null);
        final UnRegisterBrokerRequestHeader requestHeader =
            (UnRegisterBrokerRequestHeader) request.decodeCommandCustomHeader(UnRegisterBrokerRequestHeader.class);

        this.namesrvController.getRouteInfoManager().unregisterBroker(
            requestHeader.getBrokerAddr(),
            requestHeader.getBrokerName(),
            requestHeader.getBrokerId());
        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }

    private RemotingCommand getBrokerClusterInfo(ChannelHandlerContext ctx, RemotingCommand request) {
      final RemotingCommand response = RemotingCommand.createResponseCommand(null);
      byte[] content = this.namesrvController.getRouteInfoManager().getGtsManagerInfo();
      response.setBody(content);
      response.setCode(ResponseCode.SUCCESS);
      response.setRemark(null);
      return response;
    }

}
