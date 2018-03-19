package com.wf.gts.core.handler;
import java.util.concurrent.CompletableFuture;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.common.enums.TransactionRoleEnum;
import com.wf.gts.common.enums.TransactionStatusEnum;
import com.wf.gts.common.utils.IdWorkerUtils;
import com.wf.gts.core.bean.TxTransactionInfo;
import com.wf.gts.core.concurrent.BlockTask;
import com.wf.gts.core.concurrent.BlockTaskHelper;
import com.wf.gts.core.constant.Constant;
import com.wf.gts.core.service.TxManagerMessageService;
import com.wf.gts.core.util.ThreadPoolManager;

/**
 * 分布式事务运参与者
 */
@Component
public class JoinTxTransactionHandler implements TxTransactionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JoinTxTransactionHandler.class);
    private final TxManagerMessageService txManagerMessageService;
    private final PlatformTransactionManager platformTransactionManager;

    @Autowired
    public JoinTxTransactionHandler(PlatformTransactionManager platformTransactionManager, TxManagerMessageService txManagerMessageService) {
        this.platformTransactionManager = platformTransactionManager;
        this.txManagerMessageService = txManagerMessageService;
    }

    @Override
    public Object handler(ProceedingJoinPoint point, TxTransactionInfo info) throws Throwable {
      
        LOGGER.info("分布式事务参与方，开始执行,事务组id:{},方法名称:{},服务名:{}",
            info.getTxGroupId(),info.getInvocation().getMethod(),info.getInvocation().getClass().toString());
        final String taskKey = IdWorkerUtils.getInstance().createTaskKey();
        final BlockTask task = BlockTaskHelper.getInstance().getTask(taskKey);
        
        ThreadPoolManager.getInstance().addExecuteTask(() -> {
          
            final String waitKey = IdWorkerUtils.getInstance().createTaskKey();
            final BlockTask waitTask = BlockTaskHelper.getInstance().getTask(waitKey);
            TransactionStatus transactionStatus=startTransaction();
            try {
                //添加事务组信息
                if (addTxTransactionGroup(waitKey, info)) {
                    final Object res = point.proceed();
                    //设置返回数据，并唤醒之前等待的主线程
                    task.setAsyncCall(objects -> res);
                    task.signal();
                    try {
                      
                        long nana=waitTask.await(info.getTxTransaction()
                            .socketTimeout()*Constant.CONSTANT_INT_THOUSAND*Constant.CONSTANT_INT_THOUSAND);
                        
                        if(nana<=0){
                          findTransactionGroupStatus(info, waitTask);
                        }
                        commitOrRollback(transactionStatus, info, waitTask, waitKey);
                        
                    } catch (Throwable throwable) {
                        platformTransactionManager.rollback(transactionStatus);
                        LOGGER.error("分布式事务参与方,事务提交异常:{},事务组id:{}",throwable.getMessage(),info.getTxGroupId());
                    } finally {
                        BlockTaskHelper.getInstance().removeByKey(waitKey);
                    }
  
                } else {
                    platformTransactionManager.rollback(transactionStatus);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                platformTransactionManager.rollback(transactionStatus);
                task.setAsyncCall(objects -> {
                    throw throwable;
                });
                task.signal();
            }
        });
        task.await();
        LOGGER.info("参与分布式模块执行完毕,事务组id:{},方法名称:{},服务名:{}",
            info.getTxGroupId(),info.getInvocation().getMethod(),info.getInvocation().getClass().toString());
        try {
            return task.getAsyncCall().callBack();
        } finally {
            BlockTaskHelper.getInstance().removeByKey(task.getKey());
        }
    }
    
    /**
     * 功能描述: 提交或者超时回滚
     * @author: chenjy
     * @date: 2017年9月18日 下午1:28:56 
     * @param transactionStatus
     * @param info
     * @param waitTask
     * @param waitKey
     * @throws Throwable 
     */
    private void commitOrRollback(TransactionStatus transactionStatus,TxTransactionInfo info,BlockTask waitTask,String waitKey) throws Throwable{
      final Integer status = (Integer) waitTask.getAsyncCall().callBack();
      if (TransactionStatusEnum.COMMIT.getCode() == status) {
          //提交事务
          platformTransactionManager.commit(transactionStatus);
          //通知tm完成事务
          CompletableFuture.runAsync(() ->
                  txManagerMessageService
                          .AsyncCompleteCommitTxTransaction(info.getTxGroupId(), waitKey,
                                  TransactionStatusEnum.COMMIT.getCode()));

      } else if (TransactionStatusEnum.ROLLBACK.getCode()== status) {
          //如果超时了，就回滚当前事务
          platformTransactionManager.rollback(transactionStatus);
          //通知tm 自身事务需要回滚,不能提交
          CompletableFuture.runAsync(() ->
                  txManagerMessageService
                          .AsyncCompleteCommitTxTransaction(info.getTxGroupId(), waitKey,
                                  TransactionStatusEnum.ROLLBACK.getCode()));
      }
      
    }
    
    
    /**
     * 功能描述: 查找事务组状态
     * @author: chenjy
     * @date: 2017年9月18日 下午1:24:39 
     * @param info
     * @param waitTask
     * @throws Throwable 
     */
    private void  findTransactionGroupStatus(TxTransactionInfo info,BlockTask waitTask) throws Throwable{
        //如果获取通知超时了，那么就去获取事务组的状态
        final int transactionGroupStatus = txManagerMessageService.findTransactionGroupStatus(info.getTxGroupId(),info.getTxTransaction().socketTimeout());
        if (TransactionStatusEnum.PRE_COMMIT.getCode() == transactionGroupStatus ||
                TransactionStatusEnum.COMMIT.getCode() == transactionGroupStatus) {
            LOGGER.info("事务组id：{}，自动超时，获取事务组状态为提交，进行提交!", info.getTxGroupId());
            waitTask.setAsyncCall(objects -> TransactionStatusEnum.COMMIT.getCode());
        } else {
            LOGGER.info("事务组id：{}，自动超时进行回滚!", info.getTxGroupId());
            waitTask.setAsyncCall(objects -> TransactionStatusEnum.ROLLBACK.getCode());
        }
        LOGGER.error("从redis查询事务状态为:{}", transactionGroupStatus);
    }

    
    /**
     * 功能描述: 开启事务
     * @author: chenjy
     * @date: 2017年9月15日 下午5:48:29 
     * @return
     */
    private TransactionStatus startTransaction(){
      DefaultTransactionDefinition def = new DefaultTransactionDefinition();
      def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
      TransactionStatus transactionStatus = platformTransactionManager.getTransaction(def);
      return transactionStatus;
    }
    
    
    /**
     * 功能描述: 添加事务组
     * @author: chenjy
     * @date:   2017年9月15日 下午5:48:49 
     * @param   waitKey
     * @param   info
     * @return
     * @throws Throwable 
     */
    private boolean addTxTransactionGroup(String waitKey,TxTransactionInfo info) throws Throwable{
      TxTransactionItem item = new TxTransactionItem();
      item.setTaskKey(waitKey);
      item.setTransId(IdWorkerUtils.getInstance().createUUID());
      item.setStatus(TransactionStatusEnum.BEGIN.getCode());//开始事务
      item.setRole(TransactionRoleEnum.ACTOR.getCode());//参与者
      item.setTxGroupId(info.getTxGroupId());
      return txManagerMessageService.addTxTransaction(info.getTxGroupId(), item,info.getTxTransaction().socketTimeout());
    }


}
