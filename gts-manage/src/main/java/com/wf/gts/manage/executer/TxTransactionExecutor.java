package com.wf.gts.manage.executer;

public interface TxTransactionExecutor {
  
  /**
   * 回滚整个事务组
   *
   * @param txGroupId 事务组id
   */
  void rollBack(String txGroupId);


  /**
   * 事务预提交
   *
   * @param txGroupId 事务组id
   * @return true 成功 false 失败
   */
  Boolean preCommit(String txGroupId);

}
