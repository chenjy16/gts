package com.wf.gts.core.handler;
import java.util.ArrayList;
import java.util.List;
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

import com.wf.gts.common.beans.TransactionInvocation;
import com.wf.gts.common.beans.TransGroup;
import com.wf.gts.common.beans.TransItem;
import com.wf.gts.common.enums.TransRoleEnum;
import com.wf.gts.common.enums.TransStatusEnum;
import com.wf.gts.common.utils.IdWorkerUtils;
import com.wf.gts.core.bean.GtsTransInfo;
import com.wf.gts.core.service.GtsMessageService;
import com.wf.gts.core.util.TxTransactionLocal;

/**
 * 开始运行分布式事务
 * 分布式事务发起者
 */
@Component
public class StartGtsTransHandler implements GtsTransHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartGtsTransHandler.class);
    private final GtsMessageService gtsMessageService;
    private final PlatformTransactionManager platformTransactionManager;
    
    @Autowired(required = false)
    public StartGtsTransHandler(GtsMessageService gtsMessageService,PlatformTransactionManager platformTransactionManager) {
        this.gtsMessageService = gtsMessageService;
        this.platformTransactionManager = platformTransactionManager;
    }

    
    @Override
    public Object handler(ProceedingJoinPoint point, GtsTransInfo info) throws Throwable {
        LOGGER.info("tx-transaction start,事务发起类");
        String groupId = IdWorkerUtils.getInstance().createGroupId();
        //设置事务组ID
        TxTransactionLocal.getInstance().setTxGroupId(groupId);
        String waitKey = IdWorkerUtils.getInstance().createTaskKey();
        //创建事务组信息
        Boolean success = gtsMessageService.saveTxTransactionGroup(newTxTransactionGroup(groupId, waitKey,info.getInvocation()),info.getTxTransaction().socketTimeout());
        if (success) {
            TransactionStatus transactionStatus=createTransactionStatus();
            try {
                long startTime = System.currentTimeMillis();
                Object res = point.proceed();
                long runTime = System.currentTimeMillis() - startTime;
                if(info.getTxTransaction().clientTransTimeout()< runTime ){
                  throw new RuntimeException("方法执行超时");
                }
                commit(transactionStatus, groupId, info,waitKey);
                LOGGER.info("tx-transaction end,  事务发起类");
                return res;
            } catch (Throwable throwable) {
                rollbackForAll(transactionStatus, groupId,info.getTxTransaction().socketTimeout());
                LOGGER.info("事务发起类报错:{}",throwable);
                throw throwable;
            } finally {
                TxTransactionLocal.getInstance().removeTxGroupId();
            }
        }
        return null;
    }

    /**
     * 功能描述: 提交事务
     * @author: chenjy
     * @date: 2017年9月18日 下午1:13:22 
     * @param transactionStatus
     * @param groupId
     * @param info
     * @param waitKey
     * @throws Throwable 
     */
    private void  commit(TransactionStatus transactionStatus,String groupId, GtsTransInfo info,String waitKey) throws Throwable{
        
        Boolean commit = gtsMessageService.preCommitTxTransaction(groupId,info.getTxTransaction().socketTimeout());
        if (commit) {
            platformTransactionManager.commit(transactionStatus);
            //通知tm完成事务
            CompletableFuture.runAsync(() ->
                      gtsMessageService
                            .AsyncCompleteCommitTxTransaction(groupId, waitKey,
                                    TransStatusEnum.COMMIT.getCode()));
        } else {
            LOGGER.info("预提交失败!");
            platformTransactionManager.rollback(transactionStatus);
        }
    }
    
    /**
     * 功能描述: 回滚所有事物
     * @author: chenjy
     * @date: 2017年9月18日 上午11:37:58 
     * @param transactionStatus
     * @param groupId
     * @throws Throwable 
     */
    private void rollbackForAll(TransactionStatus transactionStatus,String groupId,long timeout) throws Throwable{
        //如果有异常 当前项目事务进行回滚 ，同时通知tm 整个事务失败
        platformTransactionManager.rollback(transactionStatus);
        //通知tm整个事务组失败，需要回滚，（回滚那些正常提交的模块，他们正在等待通知。。。。）
        gtsMessageService.rollBackTxTransaction(groupId,timeout);
    }
    
    
    
    /**
     * 功能描述: 创建事务
     * @author: chenjy
     * @date: 2017年9月18日 上午11:37:23 
     * @return
     */
    private TransactionStatus  createTransactionStatus(){
      DefaultTransactionDefinition def = new DefaultTransactionDefinition();
      def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
      TransactionStatus transactionStatus = platformTransactionManager.getTransaction(def);
      return transactionStatus;
    }
    
    
    /**
     * 功能描述: 创建事务组
     * @author: chenjy
     * @date: 2017年9月18日 上午11:37:37 
     * @param groupId
     * @param taskKey
     * @return
     */
    private TransGroup newTxTransactionGroup(String groupId, String taskKey,TransactionInvocation invocation) {
        //创建事务组信息
        TransGroup txTransactionGroup = new TransGroup();
        txTransactionGroup.setId(groupId);
        List<TransItem> items = new ArrayList<>(2);
        //tmManager 用redis hash 结构来存储 整个事务组的状态做为hash结构的第一条数据
        TransItem groupItem = new TransItem();
        groupItem.setStatus(TransStatusEnum.BEGIN.getCode());//整个事务组状态为开始
        groupItem.setTransId(groupId); //设置事务id为组的id  即为 hashKey
        groupItem.setTaskKey(groupId);
        groupItem.setRole(TransRoleEnum.START.getCode());
        items.add(groupItem);
        
        TransItem item = new TransItem();
        item.setTaskKey(taskKey);
        item.setTransId(IdWorkerUtils.getInstance().createUUID());
        item.setRole(TransRoleEnum.START.getCode());
        item.setStatus(TransStatusEnum.BEGIN.getCode());
        item.setTxGroupId(groupId);
        items.add(item);
        txTransactionGroup.setItemList(items);
        return txTransactionGroup;
    }

}
