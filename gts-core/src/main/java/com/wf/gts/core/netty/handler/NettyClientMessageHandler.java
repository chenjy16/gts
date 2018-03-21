package com.wf.gts.core.netty.handler;
import java.util.List;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.wf.gts.common.beans.HeartBeat;
import com.wf.gts.common.beans.TxTransactionGroup;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.common.enums.NettyMessageActionEnum;
import com.wf.gts.common.enums.NettyResultEnum;
import com.wf.gts.common.utils.IdWorkerUtils;
import com.wf.gts.core.concurrent.BlockTask;
import com.wf.gts.core.concurrent.BlockTaskHelper;
import com.wf.gts.core.config.ClientConfig;
import com.wf.gts.core.constant.Constant;
import com.wf.gts.core.exception.SocketTimeoutException;
import com.wf.gts.core.netty.NettyClient;
import com.wf.gts.core.util.SpringBeanUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;


@Component
@ChannelHandler.Sharable
public class NettyClientMessageHandler extends ChannelInboundHandlerAdapter {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientMessageHandler.class);
    
    /**
     * false 未链接
     * true 连接中
     */
    public volatile static boolean net_state = false;
    private static volatile ChannelHandlerContext ctx;
    private static final HeartBeat heartBeat = new HeartBeat();
    private ClientConfig txConfig;
    
    public void setTxConfig(ClientConfig txConfig) {
        this.txConfig = txConfig;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, final Object msg) throws Exception {
        net_state = true;
        HeartBeat heartBeat = (HeartBeat) msg;
        LOGGER.debug("接收服务端据命令为,执行的动作为:{}", heartBeat.getAction());
        final NettyMessageActionEnum actionEnum = NettyMessageActionEnum.acquireByCode(heartBeat.getAction());
        try {
          
            switch (actionEnum) {
                case HEART://心跳动作
                    break;
                case RECEIVE://客户端接收动作
                    receivedCommand(heartBeat.getKey(), heartBeat.getResult());
                    break;
                case ROLLBACK://收到服务端的回滚指令
                    notify(heartBeat);
                    break;
                case GET_TRANSACTION_GROUP_STATUS:
                    final BlockTask blockTask = BlockTaskHelper.getInstance().getTask(heartBeat.getKey());
                    final TxTransactionGroup txTransactionGroup = heartBeat.getTxTransactionGroup();
                    blockTask.setAsyncCall(objects -> txTransactionGroup.getStatus());
                    blockTask.signal();
                    break;

            }
        } finally {
            ReferenceCountUtil.release(msg);
        }


    }
    
    
    private void notify(HeartBeat heartBeat) {
        final List<TxTransactionItem> txTransactionItems = heartBeat.getTxTransactionGroup().getItemList();
        if (!CollectionUtils.isEmpty(txTransactionItems)) {
            final TxTransactionItem item = txTransactionItems.get(0);
            final BlockTask task = BlockTaskHelper.getInstance().getTask(item.getTaskKey());
            task.setAsyncCall(objects -> item.getStatus());
            task.signal();
        }
    }

    
    private void receivedCommand(String key, int result) {
        final BlockTask blockTask = BlockTaskHelper.getInstance().getTask(key);
        if (Objects.nonNull(blockTask)) {
            blockTask.setAsyncCall(objects -> result == NettyResultEnum.SUCCESS.getCode());
            blockTask.signal();
        }
    }

    

    /**
     * 向TxManager 发生消息
     * @param heartBeat 定义的数据传输对象
     * @return Object
     * @throws Throwable 
     */
    public Object sendTxManagerMessage(HeartBeat heartBeat,long timeout) throws Throwable {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            final String sendKey = IdWorkerUtils.getInstance().createTaskKey();
            BlockTask sendTask = BlockTaskHelper.getInstance().getTask(sendKey);
            heartBeat.setKey(sendKey);
            ctx.writeAndFlush(heartBeat);
            //发送线程在此等待，等tm是否正确返回（正确返回唤醒）
            long nana=sendTask.await(timeout*Constant.CONSTANT_INT_THOUSAND*Constant.CONSTANT_INT_THOUSAND);
            if(nana<=0){
                sendTask.setAsyncCall(objects -> {
                  throw new SocketTimeoutException("socket等待超时,超时时间"+timeout+"ms");
                });
            }
            try {
                return sendTask.getAsyncCall().callBack();
            }finally {
                BlockTaskHelper.getInstance().removeByKey(sendKey);
            }
        } else {
            return null;
        }

    }

    
    /**
     * 向TxManager 异步发送消息
     * @param heartBeat 定义的数据传输对象
     */
    public void AsyncSendTxManagerMessage(HeartBeat heartBeat) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            ctx.writeAndFlush(heartBeat);
        }
    }

}
