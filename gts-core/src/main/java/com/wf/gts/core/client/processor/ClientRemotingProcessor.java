package com.wf.gts.core.client.processor;
import com.wf.gts.core.client.MQClientInstance;
import com.wf.gts.remoting.exception.RemotingCommandException;
import com.wf.gts.remoting.netty.NettyRequestProcessor;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import io.netty.channel.ChannelHandlerContext;

public class ClientRemotingProcessor implements NettyRequestProcessor {
  
    private final MQClientInstance mqClientFactory;

    public ClientRemotingProcessor(final MQClientInstance mqClientFactory) {
        this.mqClientFactory = mqClientFactory;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx,
        RemotingCommand request) throws RemotingCommandException {
        switch (request.getCode()) {
            case RequestCode.ROLLBACK_TRANSGROUP:
                return this.checkTransactionState(ctx, request);
            default:
                break;
        }
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    public RemotingCommand checkTransactionState(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
       
      return null;
    }

   
}
