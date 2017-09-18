package com.wf.gts.manage.executer.impl;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.wf.gts.common.beans.ChannelSender;
import com.wf.gts.common.beans.HeartBeat;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.common.enums.TransactionStatusEnum;
import com.wf.gts.common.utils.ExecutorMessageTool;
import com.wf.gts.common.utils.OkHttpTools;
import com.wf.gts.manage.constant.Constant;
import com.wf.gts.manage.service.TxManagerService;


@Component
public class TxTransactionExecutorService extends AbstractTxTransactionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxTransactionExecutorService.class);
    private static final Gson gson = new Gson();


    @Autowired
    public TxTransactionExecutorService(TxManagerService txManagerService) {
        super.setTxManagerService(txManagerService);
    }


    /**
     * 执行回滚动作
     * @param txGroupId          事务组id
     * @param txTransactionItems 连接到当前tm的channel信息
     * @param elseItems          连接到其他tm的channel信息
     */
    @Override
    protected void doRollBack(String txGroupId, List<TxTransactionItem> txTransactionItems, List<TxTransactionItem> elseItems) {
        try {
            if (CollectionUtils.isNotEmpty(txTransactionItems)) {
              
                final CompletableFuture[] cfs = txTransactionItems
                        .stream()
                        .map(item ->
                                CompletableFuture.runAsync(() -> {
                                  
                                    ChannelSender channelSender = new ChannelSender();
                                    
                                    HeartBeat heartBeat = ExecutorMessageTool.buildMessage(item, channelSender,
                                            TransactionStatusEnum.ROLLBACK);
                                    
                                    if (Objects.nonNull(channelSender.getChannel())) {
                                        channelSender.getChannel().writeAndFlush(heartBeat);
                                    } else {
                                        LOGGER.error("txManger rollback指令失败，channel为空，事务组id：{}, 事务taskId为:{}",
                                                txGroupId, item.getTaskKey());
                                    }
                                    
                                }).whenComplete((v, e) ->
                                    LOGGER.info("txManger 成功发送rollback指令 事务taskId为：{}", item.getTaskKey())
                                ))
                        
                        
                        .toArray(CompletableFuture[]::new);
                
                
                CompletableFuture.allOf(cfs).join();
                LOGGER.info("txManger 成功发送rollback指令 事务组id为：{}",txGroupId);
            }
            
            
            httpExecute(elseItems, TransactionStatusEnum.ROLLBACK);

        } catch (Exception e) {
            LOGGER.info("txManger 发送rollback指令异常：{} ", e);
        }
    }

    
    
    
    
    

    /**
     * 执行提交动作
     *
     * @param txGroupId          事务组id
     * @param txTransactionItems 连接到当前tm的channel信息
     * @param elseItems          连接到其他tm的channel信息
     */
    @Override
    protected void doCommit(String txGroupId, List<TxTransactionItem> txTransactionItems, List<TxTransactionItem> elseItems) {
        try {
            txTransactionItems.forEach(item -> {
                ChannelSender sender = new ChannelSender();
                HeartBeat heartBeat = ExecutorMessageTool.buildMessage(item, sender, TransactionStatusEnum.COMMIT);
                if (Objects.nonNull(sender.getChannel())) {
                    sender.getChannel().writeAndFlush(heartBeat);
                    LOGGER.info("txManger 成功发送doCommit指令 事务taskId为：{}", item.getTaskKey());
                } else {
                    LOGGER.info("txManger 发送doCommit指令失败，channel为空，事务组id：{}, 事务taskId为:{}", txGroupId, item.getTaskKey());
                }

            });

            httpExecute(elseItems, TransactionStatusEnum.COMMIT);

        } catch (Exception e) {
            LOGGER.info("txManger 发送doCommit指令异常:{} ", e);
        }

    }


    /**
     * 获取当前连接的channel  为什么？
     * 因为如果tm是集群环境，可能业务的channel对象连接到不同的tm
     * 那么当前的tm可没有其他业务模块的长连接信息，那么就应该做：
     * 1.检查当前tm的channel状态，并只提交当前渠道的命令
     * 2.通知 连接到其他tm的channel，执行命令
     * 通过http 执行 连接到其他tm 的channel
     * @param elseItems      其他的渠道集合
     * @param transactionStatusEnum 执行动作
     */
    private void httpExecute(List<TxTransactionItem> elseItems, TransactionStatusEnum transactionStatusEnum) {
        if (CollectionUtils.isNotEmpty(elseItems)) {
            //按照域名进行分组
            final Map<String, List<TxTransactionItem>> senderItems =
                    elseItems.stream().collect(Collectors.groupingBy(TxTransactionItem::getTmDomain));
           
            senderItems.forEach((k, v) -> {
                try {
                    if (transactionStatusEnum.getCode() == TransactionStatusEnum.COMMIT.getCode()) {
                        OkHttpTools.getInstance().post(String.format(Constant.httpCommit, k), gson.toJson(v));
                    
                    } else if (transactionStatusEnum.getCode() == TransactionStatusEnum.ROLLBACK.getCode()) {
                        OkHttpTools.getInstance().post(String.format(Constant.httpRollback, k), gson.toJson(v));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
    }


}
