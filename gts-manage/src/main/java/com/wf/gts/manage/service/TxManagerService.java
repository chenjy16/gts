package com.wf.gts.manage.service;
import java.util.Collection;
import java.util.List;
import com.wf.gts.common.beans.TxTransactionGroup;
import com.wf.gts.common.beans.TxTransactionItem;


public interface TxManagerService {
  

  /**
   * 保存事务组 在事务发起方的时候进行调用
   * @param txTransactionGroup 事务组
   * @return true 成功 false 失败
   */
  Boolean saveTxTransactionGroup(TxTransactionGroup txTransactionGroup);


  /**
   * 往事务组添加事务
   * @param txGroupId         事务组id
   * @param txTransactionItem 子事务项
   * @return true 成功 false 失败
   */
  Boolean addTxTransaction(String txGroupId, TxTransactionItem txTransactionItem);

  /**
   * 根据事务组id 获取所有的子项目
   * @param txGroupId 事务组id
   * @return List<TxTransactionItem>
   */
  List<TxTransactionItem> listByTxGroupId(String txGroupId);


  /**
   * 删除事务组信息  当回滚的时候 或者事务组完全提交的时候
   * @param txGroupId txGroupId 事务组id
   */
  void removeRedisByTxGroupId(String txGroupId);


  /**
   * 更新事务状态
   * @param key  redis key 也就是txGroupId
   * @param hashKey  也就是taskKey
   * @param status  事务状态
   * @return true 成功 false 失败
   */
  Boolean updateTxTransactionItemStatus(String key,String hashKey,int status);

  /**
   * 获取事务组的状态
   * @param txGroupId 事务组id
   * @return 事务组状态
   */
  int findTxTransactionGroupStatus(String txGroupId);
  
  
  /**
   * 功能描述: 事务组列表
   * @author: chenjy
   * @date: 2017年9月19日 下午3:49:33 
   * @return
   */
  Collection<String> listTxGroupId();




}
