package com.wf.gts.manage.netty.handler;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.wf.gts.common.SocketManager;
import com.wf.gts.common.beans.HeartBeat;
import com.wf.gts.common.beans.TxTransactionGroup;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.common.enums.NettyMessageActionEnum;
import com.wf.gts.common.enums.NettyResultEnum;
import com.wf.gts.manage.domain.Address;
import com.wf.gts.manage.executer.TxTransactionExecutor;
import com.wf.gts.manage.service.TxManagerService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * netty服务端接收消息
 */
@ChannelHandler.Sharable
@Component
public class NettyServerMessageHandler extends ChannelInboundHandlerAdapter {

    private final TxManagerService txManagerService;
    private final TxTransactionExecutor txTransactionExecutor;

    @Autowired
    public NettyServerMessageHandler(TxManagerService txManagerService, TxTransactionExecutor txTransactionExecutor) {
        this.txManagerService = txManagerService;
        this.txTransactionExecutor = txTransactionExecutor;
    }

    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HeartBeat hb = (HeartBeat) msg;
        TxTransactionGroup txTransactionGroup = hb.getTxTransactionGroup();
        try {
            final NettyMessageActionEnum actionEnum = NettyMessageActionEnum.acquireByCode(hb.getAction());
            Boolean success;
            switch (actionEnum) {
                case HEART:
                    hb.setAction(NettyMessageActionEnum.HEART.getCode());
                    ctx.writeAndFlush(hb);
                    break;
                case CREATE_GROUP:
                    final List<TxTransactionItem> items = txTransactionGroup.getItemList();
                    if (CollectionUtils.isNotEmpty(items)) {
                        String modelName = ctx.channel().remoteAddress().toString();
                        //这里创建事务组的时候，事务组也作为第一条数据来存储
                        //第二条数据才是发起方因此是get(1)
                        final TxTransactionItem item = items.get(1);
                        item.setModelName(modelName);
                        item.setTmDomain(Address.getInstance().getDomain());
                    }
                    success = txManagerService.saveTxTransactionGroup(txTransactionGroup);
                    ctx.writeAndFlush(buildSendMessage(hb.getKey(), success));
                    break;
                case ADD_TRANSACTION:
                    final List<TxTransactionItem> itemList = txTransactionGroup.getItemList();
                    if (CollectionUtils.isNotEmpty(itemList)) {
                        String modelName = ctx.channel().remoteAddress().toString();
                        final TxTransactionItem item = itemList.get(0);
                        item.setModelName(modelName);
                        item.setTmDomain(Address.getInstance().getDomain());
                        success = txManagerService.addTxTransaction(txTransactionGroup.getId(), item);
                        ctx.writeAndFlush(buildSendMessage(hb.getKey(), success));
                    }
                    break;
                case GET_TRANSACTION_GROUP_STATUS:
                  
                    final int status = txManagerService.findTxTransactionGroupStatus(txTransactionGroup.getId());
                    txTransactionGroup.setStatus(status);
                    hb.setTxTransactionGroup(txTransactionGroup);
                    ctx.writeAndFlush(hb);
                    
                    break;
                case ROLLBACK:
                    ctx.writeAndFlush(buildSendMessage(hb.getKey(), true));
                    //收到客户端的回滚通知  此通知为事务发起（start）里面通知的
                    final String groupId = txTransactionGroup.getId();
                    txTransactionExecutor.rollBack(groupId);
                    break;
                case PRE_COMMIT:
                    ctx.writeAndFlush(buildSendMessage(hb.getKey(), true));
                    txTransactionExecutor.preCommit(txTransactionGroup.getId());
                    break;
                case COMPLETE_COMMIT:
                    final List<TxTransactionItem> its = txTransactionGroup.getItemList();
                    txManagerService.updateTxTransactionItemStatus(txTransactionGroup.getId(), its.get(0).getTaskKey(),
                            its.get(0).getStatus());
                    break;
                default:
                    hb.setAction(NettyMessageActionEnum.HEART.getCode());
                    ctx.writeAndFlush(hb);
                    break;
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    
    /**
     * 建立连接
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //是否到达最大上线连接数
        if (SocketManager.getInstance().isAllowConnection()) {
            SocketManager.getInstance().addClient(ctx.channel());
        } else {
            ctx.close();
        }
    }
    
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }
    
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        SocketManager.getInstance().removeClient(ctx.channel());
        super.channelUnregistered(ctx);
    }

    /**
     * 读完成
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //心跳配置
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.close();
            }
        }
    }

    private HeartBeat buildSendMessage(String key, Boolean success) {
        HeartBeat HB = new HeartBeat();
        HB.setKey(key);
        HB.setAction(NettyMessageActionEnum.RECEIVE.getCode());
        if (success) {
            HB.setResult(NettyResultEnum.SUCCESS.getCode());
        } else {
            HB.setResult(NettyResultEnum.FAIL.getCode());
        }
        return HB;
    }

}