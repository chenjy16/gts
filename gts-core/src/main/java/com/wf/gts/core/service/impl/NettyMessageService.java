package com.wf.gts.core.service.impl;
import java.util.Collections;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wf.gts.common.beans.HeartBeat;
import com.wf.gts.common.beans.TxTransactionGroup;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.common.enums.NettyMessageActionEnum;
import com.wf.gts.common.enums.TransactionStatusEnum;
import com.wf.gts.core.netty.handler.NettyClientMessageHandler;
import com.wf.gts.core.service.TxManagerMessageService;

/**
 * netty与txManager进行消息通信
 */
@Service
public class NettyMessageService implements TxManagerMessageService {

    private final NettyClientMessageHandler nettyClientMessageHandler;

    @Autowired
    public NettyMessageService(NettyClientMessageHandler nettyClientMessageHandler) {
        this.nettyClientMessageHandler = nettyClientMessageHandler;
    }

    
    
    /**
     * 保存事务组 在事务发起方的时候进行调用
     * @param txTransactionGroup 事务组
     * @return true 成功 false 失败
     * @throws Throwable 
     */
    @Override
    public Boolean saveTxTransactionGroup(TxTransactionGroup txTransactionGroup,long timeout) throws Throwable {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.CREATE_GROUP.getCode());
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.sendTxManagerMessage(heartBeat,timeout);
        if (Objects.nonNull(object)) {
            return (Boolean) object;
        }
        return false;

    }

    /**
     * 往事务组添加事务
     * @param txGroupId         事务组id
     * @param txTransactionItem 子事务项
     * @return true 成功 false 失败
     * @throws Throwable 
     */
    @Override
    public Boolean addTxTransaction(String txGroupId, TxTransactionItem txTransactionItem,long timeout) throws Throwable {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.ADD_TRANSACTION.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);
        txTransactionGroup.setItemList(Collections.singletonList(txTransactionItem));
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.sendTxManagerMessage(heartBeat,timeout);
        if (Objects.nonNull(object)) {
            return (Boolean) object;
        }
        return false;
    }

    /**
     * 获取事务组状态
     * @param txGroupId 事务组id
     * @return 事务组状态
     * @throws Throwable 
     */
    @Override
    public int findTransactionGroupStatus(String txGroupId,long timeout) throws Throwable {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.GET_TRANSACTION_GROUP_STATUS.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.sendTxManagerMessage(heartBeat,timeout);
        if (Objects.nonNull(object)) {
            return (Integer) object;
        }
        return TransactionStatusEnum.ROLLBACK.getCode();

    }

 

    /**
     * 通知tm 回滚整个事务组
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     * @throws Throwable 
     */
    @Override
    public Boolean rollBackTxTransaction(String txGroupId,long timeout) throws Throwable {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.ROLLBACK.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        txTransactionGroup.setId(txGroupId);
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.sendTxManagerMessage(heartBeat,timeout);
        if (Objects.nonNull(object)) {
            return (Boolean) object;
        }
        return false;
    }

    /**
     * 通知tm自身业务已经执行完成，等待提交事务
     * tm 收到后，进行pre_commit  再进行doCommit
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     * @throws Throwable 
     */
    @Override
    public Boolean preCommitTxTransaction(String txGroupId,long timeout) throws Throwable {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.PRE_COMMIT.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setStatus(TransactionStatusEnum.PRE_COMMIT.getCode());
        txTransactionGroup.setId(txGroupId);
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.sendTxManagerMessage(heartBeat,timeout);
        if (Objects.nonNull(object)) {
            return (Boolean) object;
        }
        return false;
    }
   
    /**
     * 异步完成自身事务的提交
     * @param txGroupId 事务组id
     * @param taskKey   子事务的taskKey
     * @param status    状态  
     */
    @Override
    public void AsyncCompleteCommitTxTransaction(String txGroupId, String taskKey, int status) {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.COMPLETE_COMMIT.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);
        TxTransactionItem item = new TxTransactionItem();
        item.setTaskKey(taskKey);
        item.setStatus(status);
        txTransactionGroup.setItemList(Collections.singletonList(item));
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        nettyClientMessageHandler.AsyncSendTxManagerMessage(heartBeat);
    }


}
