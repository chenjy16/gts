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

import com.alibaba.fastjson.JSON;
import com.wf.gts.common.beans.TransGroup;
import com.wf.gts.common.beans.TransItem;
import com.wf.gts.common.enums.TransRoleEnum;
import com.wf.gts.common.enums.TransStatusEnum;
import com.wf.gts.manage.ManageController;
import com.wf.gts.manage.client.ClientChannelInfo;
import com.wf.gts.manage.exception.GtsManageException;
import com.wf.gts.manage.executer.GtsTransExecutor;
import com.wf.gts.manage.service.GtsManagerService;
import com.wf.gts.remoting.RemotingServer;
import com.wf.gts.remoting.exception.RemotingSendRequestException;
import com.wf.gts.remoting.exception.RemotingTimeoutException;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RemotingSerializable;
import com.wf.gts.remoting.protocol.RequestCode;

@Component
public class GtsTransExecutorImpl implements GtsTransExecutor{
  
    private static final Logger LOGGER = LoggerFactory.getLogger(GtsTransExecutorImpl.class);
    
    @Autowired
    private GtsManagerService gtsManagerService;
    
    /**
     * 回滚整个事务组
     * @param txGroupId 事务组id
     */
    @Override
    public void rollBack(String txGroupId,ManageController manageController) throws GtsManageException{
        List<TransItem> txTransactionItems = gtsManagerService.listByTxGroupId(txGroupId);
        txTransactionItems= filterData(txTransactionItems);
        if(CollectionUtils.isEmpty(txTransactionItems)){
          throw new GtsManageException(10001, "事务组信息不完整");
        }
        HashMap<String,ClientChannelInfo> channels=manageController.getClientChannelManager().getAllClientChannelTable();
        LOGGER.info("rollBack:{}",JSON.toJSONString(channels));
        RemotingServer remotingServer=manageController.getRemotingServer();
        doRollBack(txGroupId, txTransactionItems,channels,remotingServer);
    }


    /**
     * 
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     * @throws GtsManageException 
     */
    @Override
    public Boolean preCommit(String txGroupId,ManageController manageController) throws GtsManageException {
        List<TransItem> txTransactionItems = gtsManagerService.listByTxGroupId(txGroupId);
        txTransactionItems= filterData(txTransactionItems);
        if(CollectionUtils.isEmpty(txTransactionItems)){
          throw new GtsManageException(10001, "事务组信息不完整");
        }
        HashMap<String,ClientChannelInfo> channels=manageController.getClientChannelManager().getAllClientChannelTable();
        LOGGER.info("pricomit:{}",JSON.toJSONString(channels));
        
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
    
    
    
    /**
     * 功能描述: 过滤掉发起方的数据，发起方已经回滚，不需要再通信进行回滚
     * @author: chenjy
     * @date: 2018年3月26日 下午4:12:16 
     * @param txTransactionItems
     * @return
     */
    private List<TransItem> filterData(List<TransItem> txTransactionItems){
      List<TransItem> collect = txTransactionItems.stream()
              .filter(item -> item.getRole() != TransRoleEnum.START.getCode())
              .collect(Collectors.toList());
      if (CollectionUtils.isEmpty(collect)) {
          return null;
      }
      return collect;
    }

    
   /**
    * 功能描述: 检测客户端通道状态
    * @author: chenjy
    * @date: 2018年3月27日 上午10:30:22 
    * @param txTransactionItems
    * @param channels
    * @return
    */
    private Boolean checkChannel(List<TransItem> txTransactionItems,HashMap<String,ClientChannelInfo> channels) {
        if (CollectionUtils.isNotEmpty(txTransactionItems)) {
            List<TransItem> collect = txTransactionItems.stream().filter(item -> {
              
                ClientChannelInfo channelInfo = channels.get(item.getModelName());
                return Objects.nonNull(channelInfo.getChannel()) && (channelInfo.getChannel().isActive() || item.getStatus() != TransStatusEnum.ROLLBACK.getCode());
            }).collect(Collectors.toList());
            return txTransactionItems.size() == collect.size();
        }
        return true;
    }

    
    /**
     * 功能描述: 执行回滚动作
     * @author: chenjy
     * @date: 2018年3月27日 上午10:30:02 
     * @param txGroupId
     * @param txTransactionItems
     * @param channels
     * @param remotingServer
     */
    private void doRollBack(String txGroupId, List<TransItem> txTransactionItems,HashMap<String,ClientChannelInfo> channels,RemotingServer remotingServer) {
        try {
            if (CollectionUtils.isNotEmpty(txTransactionItems)) {
                 CompletableFuture[] cfs = txTransactionItems
                        .stream()
                        .map(item ->
                                CompletableFuture.runAsync(() -> {
                                    ClientChannelInfo channelinfo=channels.get(item.getModelName());
                                    
                                    try {
                                      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.ROLLBACK_TRANSGROUP, null);
                                      TransGroup txTransactionGroup = new TransGroup();
                                      txTransactionGroup.setStatus(TransStatusEnum.ROLLBACK.getCode());
                                      txTransactionGroup.setItemList(Collections.singletonList(item));
                                      
                                      byte[] body = RemotingSerializable.encode(txTransactionGroup);
                                      request.setBody(body);
                                      RemotingCommand res=remotingServer.invokeSync(channelinfo.getChannel(), request, 3000);
                                    } catch (RemotingSendRequestException | RemotingTimeoutException
                                        | InterruptedException e1) {
                                      LOGGER.error("txManger rollback指令失败，channel为空，事务组id：{}, 事务taskId为:{}",txGroupId, item.getTaskKey());
                                    }
                                    
                                    
                                }).whenComplete((v, e) ->
                                    LOGGER.info("txManger 成功发送rollback指令 事务taskId为：{}", item.getTaskKey())
                                ))
                        .toArray(CompletableFuture[]::new);
                 
                CompletableFuture.allOf(cfs).join();
                //更新事务组状态
                gtsManagerService.updateTxTransactionItemStatus(txGroupId, txGroupId, TransStatusEnum.ROLLBACK.getCode());
            }
        } catch (Exception e) {
            LOGGER.error("txManger 发送rollback指令异常：{} ", e);
        }
       
    }


    /**
     * 功能描述: 执行提交动作
     * @author: chenjy
     * @date: 2018年3月27日 上午10:29:47 
     * @param txGroupId
     * @param txTransactionItems
     * @param channels
     * @param remotingServer
     */
    private void doCommit(String txGroupId, List<TransItem> txTransactionItems,HashMap<String,ClientChannelInfo> channels,RemotingServer remotingServer) {
        txTransactionItems.forEach(item -> {
            ClientChannelInfo channelinfo=channels.get(item.getModelName());
            try {
              TransGroup transGroup = new TransGroup();
              item.setStatus(TransStatusEnum.COMMIT.getCode());
              transGroup.setStatus(TransStatusEnum.COMMIT.getCode());
              transGroup.setItemList(Collections.singletonList(item));
              
              RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.COMMIT_TRANS, null);
              byte[] body = RemotingSerializable.encode(transGroup);
              request.setBody(body);
              RemotingCommand res=remotingServer.invokeSync(channelinfo.getChannel(), request, 3000);
            } catch (RemotingSendRequestException | RemotingTimeoutException| InterruptedException e1) {
              LOGGER.info("txManger 发送doCommit指令失败，channel为空，事务组id：{}, 事务taskId为:{}", txGroupId, item.getTaskKey());
            }
        });
        //更新事务组状态
        gtsManagerService.updateTxTransactionItemStatus(txGroupId, txGroupId, TransStatusEnum.COMMIT.getCode());
    }
}
