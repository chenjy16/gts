package com.wf.gts.manage.executer.impl;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.wf.gts.common.beans.TxTransactionGroup;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.common.enums.TransactionRoleEnum;
import com.wf.gts.common.enums.TransactionStatusEnum;
import com.wf.gts.manage.ManageController;
import com.wf.gts.manage.client.ClientChannelInfo;
import com.wf.gts.manage.executer.TxTransactionExecutor;
import com.wf.gts.manage.service.TxManagerService;
import com.wf.gts.remoting.RemotingServer;
import com.wf.gts.remoting.exception.RemotingSendRequestException;
import com.wf.gts.remoting.exception.RemotingTimeoutException;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RemotingSerializable;
import com.wf.gts.remoting.protocol.RequestCode;

@Component
public class TxTransactionExecutorService implements TxTransactionExecutor{
  
    private static final Logger LOGGER = LoggerFactory.getLogger(TxTransactionExecutorService.class);
    
    @Autowired
    private TxManagerService txManagerService;
    
    /**
     * 回滚整个事务组
     * @param txGroupId 事务组id
     */
    @Override
    public void rollBack(String txGroupId,ManageController manageController) {
        txManagerService.updateTxTransactionItemStatus(txGroupId, txGroupId, TransactionStatusEnum.ROLLBACK.getCode());
        List<TxTransactionItem> txTransactionItems = txManagerService.listByTxGroupId(txGroupId);
        txTransactionItems= filterData(txTransactionItems);
        if(CollectionUtils.isEmpty(txTransactionItems)){
          //抛异常
        }
        HashMap<String,ClientChannelInfo> channels=manageController.getClientChannelManager().getAllClientChannelTable();
       
        
        RemotingServer remotingServer=manageController.getRemotingServer();
        doRollBack(txGroupId, txTransactionItems,channels,remotingServer);
    }


    /**
     * 事务预提交
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    @Override
    public Boolean preCommit(String txGroupId,ManageController manageController) {
      
        txManagerService.updateTxTransactionItemStatus(txGroupId, txGroupId, TransactionStatusEnum.COMMIT.getCode());
        List<TxTransactionItem> txTransactionItems = txManagerService.listByTxGroupId(txGroupId);
        txTransactionItems= filterData(txTransactionItems);
        if(CollectionUtils.isEmpty(txTransactionItems)){
          //抛异常
        }
        HashMap<String,ClientChannelInfo> channels=manageController.getClientChannelManager().getAllClientChannelTable();
        //检查各位channel 是否都激活，渠道状态不是回滚的
        final Boolean ok = checkChannel(txTransactionItems,channels);
        RemotingServer remotingServer=manageController.getRemotingServer();
        if (!ok) {
            doRollBack(txGroupId, txTransactionItems,channels,remotingServer);
        } else {
            doCommit(txGroupId, txTransactionItems,channels,remotingServer);
        }
        return true;
    }
    
    
    
    
    private List<TxTransactionItem> filterData(List<TxTransactionItem> txTransactionItems){
      //过滤掉发起方的数据，发起方已经回滚，不需要再通信进行回滚
      final List<TxTransactionItem> collect = txTransactionItems.stream()
              .filter(item -> item.getRole() != TransactionRoleEnum.START.getCode())
              .collect(Collectors.toList());
      if (CollectionUtils.isEmpty(collect)) {
          return null;
      }
      return collect;
    }

    
    /**
     * 功能描述: 检测客户端通道状态
     * @author: chenjy
     * @date: 2018年3月20日 下午3:08:38 
     * @param txTransactionItems
     * @param channels
     * @return
     */
    private Boolean checkChannel(List<TxTransactionItem> txTransactionItems,HashMap<String,ClientChannelInfo> channels) {
        if (CollectionUtils.isNotEmpty(txTransactionItems)) {
            List<TxTransactionItem> collect = txTransactionItems.stream().filter(item -> {
                ClientChannelInfo channelInfo = channels.get(item.getModelName());
                return Objects.nonNull(channelInfo.getChannel()) && (channelInfo.getChannel().isActive() || item.getStatus() != TransactionStatusEnum.ROLLBACK.getCode());
            }).collect(Collectors.toList());
            return txTransactionItems.size() == collect.size();
        }
        return true;
    }

    
    /**
     * 执行回滚动作
     * @param txGroupId          事务组id
     * @param txTransactionItems 连接到当前tm的channel信息
     */
    private void doRollBack(String txGroupId, List<TxTransactionItem> txTransactionItems,HashMap<String,ClientChannelInfo> channels,RemotingServer remotingServer) {
        try {
            if (CollectionUtils.isNotEmpty(txTransactionItems)) {
                 CompletableFuture[] cfs = txTransactionItems
                        .stream()
                        .map(item ->
                                CompletableFuture.runAsync(() -> {
                                    ClientChannelInfo channelinfo=channels.get(item.getModelName());
                                    try {
                                      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.ROLLBACK_TRANSGROUP, null);
                                      TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
                                      txTransactionGroup.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
                                      txTransactionGroup.setItemList(Collections.singletonList(item));
                                      
                                      byte[] body = RemotingSerializable.encode(txTransactionGroup);
                                      request.setBody(body);
                                      RemotingCommand res=remotingServer.invokeSync(channelinfo.getChannel(), request, 3000);
                                      //更新事务组中事务状态
                                     // txManagerService.updateTxTransactionItemStatus(item.getTxGroupId(), item.getTaskKey(),TransactionStatusEnum.ROLLBACK.getCode());
                                    } catch (RemotingSendRequestException | RemotingTimeoutException
                                        | InterruptedException e1) {
                                      LOGGER.error("txManger rollback指令失败，channel为空，事务组id：{}, 事务taskId为:{}",txGroupId, item.getTaskKey());
                                    }
                                }).whenComplete((v, e) ->
                                    LOGGER.info("txManger 成功发送rollback指令 事务taskId为：{}", item.getTaskKey())
                                ))
                        .toArray(CompletableFuture[]::new);
                 
                CompletableFuture.allOf(cfs).join();
                LOGGER.info("txManger 成功发送rollback指令 事务组id为：{}",txGroupId);
            }
        } catch (Exception e) {
            LOGGER.error("txManger 发送rollback指令异常：{} ", e);
        }
    }


    /**
     * 执行提交动作
     * @param txGroupId          事务组id
     * @param txTransactionItems 连接到当前tm的channel信息
     */
    protected void doCommit(String txGroupId, List<TxTransactionItem> txTransactionItems,HashMap<String,ClientChannelInfo> channels,RemotingServer remotingServer) {
        try {
            txTransactionItems.forEach(item -> {
                ClientChannelInfo channelinfo=channels.get(item.getModelName());
                try {
                  TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
                  item.setStatus(TransactionStatusEnum.COMMIT.getCode());
                  txTransactionGroup.setStatus(TransactionStatusEnum.COMMIT.getCode());
                  txTransactionGroup.setItemList(Collections.singletonList(item));
                  
                  RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.COMMIT_TRANS, null);
                  byte[] body = RemotingSerializable.encode(txTransactionGroup);
                  request.setBody(body);
                  RemotingCommand res=remotingServer.invokeSync(channelinfo.getChannel(), request, 3000);
                  //更新事务组中事务状态
                  //txManagerService.updateTxTransactionItemStatus(item.getTxGroupId(), item.getTaskKey(),TransactionStatusEnum.COMMIT.getCode());
                } catch (RemotingSendRequestException | RemotingTimeoutException| InterruptedException e1) {
                  LOGGER.info("txManger 发送doCommit指令失败，channel为空，事务组id：{}, 事务taskId为:{}", txGroupId, item.getTaskKey());
                }
            });
        } catch (Exception e) {
            LOGGER.info("txManger 发送doCommit指令异常:{} ", e);
        }
    }


}
