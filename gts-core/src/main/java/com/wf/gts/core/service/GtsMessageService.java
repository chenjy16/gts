package com.wf.gts.core.service;
import com.wf.gts.common.beans.TransGroup;
import com.wf.gts.common.beans.TransItem;

/**
 * 与txManage消息服务
 */
public interface GtsMessageService {

    /**
     * 保存事务组 在事务发起方的时候进行调用
     * @param txTransactionGroup 事务组
     * @return true 成功 false 失败
     */
    Boolean saveTxTransactionGroup(TransGroup txTransactionGroup,long timeout)throws Throwable;


    /**
     * 往事务组添加事务
     * @param txGroupId         事务组id
     * @param txTransactionItem 子事务项
     * @return true 成功 false 失败
     */
    Boolean addTxTransaction(String txGroupId, TransItem txTransactionItem,long timeout)throws Throwable;


    /**
     * 获取事务组状态
     * @param txGroupId 事务组id
     * @return 事务组状态
     */
    int findTransactionGroupStatus(String txGroupId,long timeout)throws Throwable;




    /**
     * 通知tm 回滚整个事务组
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    Boolean rollBackTxTransaction(String txGroupId,long timeout)throws Throwable;


    /**
     * 通知tm自身业务已经执行完成，等待提交事务
     * tm 收到后，进行pre_commit  再进行doCommit
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    Boolean preCommitTxTransaction(String txGroupId,long timeout)throws Throwable;




    /**
     * 异步完成自身事务的提交
     *
     * @param txGroupId 事务组id
     * @param taskKey   子事务的taskKey
     * @param status    状态  {@linkplain com.TransStatusEnum.transaction.common.enums.TransactionStatusEnum}
     */
    void AsyncCompleteCommitTxTransaction(String txGroupId, String taskKey,int status);


}
