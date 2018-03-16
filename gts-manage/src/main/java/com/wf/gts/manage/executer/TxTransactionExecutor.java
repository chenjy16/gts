package com.wf.gts.manage.executer;
import com.wf.gts.manage.ManageController;

public interface TxTransactionExecutor {
  
  /**
   * 回滚整个事务组
   * @param txGroupId 事务组id
   */
  void rollBack(String txGroupId,ManageController manageController);


  /**
   * 事务预提交
   * @param txGroupId 事务组id
   * @return true 成功 false 失败
   */
  Boolean preCommit(String txGroupId,ManageController manageController);

}
