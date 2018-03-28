package com.wf.gts.core.client.processor;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.wf.gts.common.beans.TransGroup;
import com.wf.gts.common.beans.TransItem;
import com.wf.gts.core.concurrent.BlockTask;
import com.wf.gts.core.concurrent.BlockTaskHelper;
import com.wf.gts.remoting.exception.RemotingCommandException;
import com.wf.gts.remoting.netty.NettyRequestProcessor;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RemotingSerializable;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;

import io.netty.channel.ChannelHandlerContext;


public class ClientRemotingProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
        switch (request.getCode()) {
            case RequestCode.ROLLBACK_TRANSGROUP:
                return rollback(ctx, request);
            case RequestCode.COMMIT_TRANS:    
                return commit(ctx, request);
            default:
                break;
        }
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    
    private RemotingCommand rollback(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
      RemotingCommand response = RemotingCommand.createResponseCommand(null);
      byte[] body = request.getBody();
      if (body != null) {
        TransGroup tx =RemotingSerializable.decode(body,TransGroup.class);
        List<TransItem> txTransactionItems = tx.getItemList();
        if (!CollectionUtils.isEmpty(txTransactionItems)) {
            TransItem item = txTransactionItems.get(0);
            BlockTask task = BlockTaskHelper.getInstance().getTask(item.getTaskKey());
            task.setAsyncCall(objects -> item.getStatus());
            task.signal();
        }
        response.setCode(ResponseCode.SUCCESS);
        response.setOpaque(request.getOpaque());
        ctx.writeAndFlush(response);
      }
      return null;
    }

    
    
    private RemotingCommand commit(ChannelHandlerContext ctx,RemotingCommand request) throws RemotingCommandException {
      byte[] body = request.getBody();
      if (body != null) {
        TransGroup tx =RemotingSerializable.decode(body,TransGroup.class);
        List<TransItem> txTransactionItems = tx.getItemList();
        if (!CollectionUtils.isEmpty(txTransactionItems)) {
            TransItem item = txTransactionItems.get(0);
            BlockTask task = BlockTaskHelper.getInstance().getTask(item.getTaskKey());
            task.setAsyncCall(objects -> item.getStatus());
            task.signal();
        }
        RemotingCommand response = RemotingCommand.createResponseCommand(null);
        response.setCode(ResponseCode.SUCCESS);
        response.setOpaque(request.getOpaque());
        ctx.writeAndFlush(response);
      }
      return null;
    }
    
    
}
