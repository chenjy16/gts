package com.wf.gts.common.utils;
import java.util.Collections;
import java.util.Objects;
import com.wf.gts.common.SocketManager;
import com.wf.gts.common.beans.ChannelSender;
import com.wf.gts.common.beans.HeartBeat;
import com.wf.gts.common.beans.TxTransactionGroup;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.common.enums.NettyMessageActionEnum;
import com.wf.gts.common.enums.TransactionStatusEnum;

import io.netty.channel.Channel;

public class ExecutorMessageTool {

  public  static HeartBeat buildMessage(TxTransactionItem item, ChannelSender channelSender, TransactionStatusEnum transactionStatusEnum) {
     
     HeartBeat heartBeat = new HeartBeat();
     
     Channel channel = SocketManager.getInstance().getChannelByModelName(item.getModelName());
     
     if (Objects.nonNull(channel)) {
         if (channel.isActive()) {
             channelSender.setChannel(channel);
         }
     }
     TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
     if(TransactionStatusEnum.ROLLBACK.getCode()==transactionStatusEnum.getCode()){
       
         heartBeat.setAction(NettyMessageActionEnum.ROLLBACK.getCode());
         item.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
         txTransactionGroup.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
         
     }else if(TransactionStatusEnum.COMMIT.getCode()==transactionStatusEnum.getCode()){
       
         heartBeat.setAction(NettyMessageActionEnum.COMPLETE_COMMIT.getCode());
         item.setStatus(TransactionStatusEnum.COMMIT.getCode());
         txTransactionGroup.setStatus(TransactionStatusEnum.COMMIT.getCode());
     }
     txTransactionGroup.setItemList(Collections.singletonList(item));
     heartBeat.setTxTransactionGroup(txTransactionGroup);
     return heartBeat;
 }
}
