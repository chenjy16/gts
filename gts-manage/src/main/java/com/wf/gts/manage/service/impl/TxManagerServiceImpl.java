package com.wf.gts.manage.service.impl;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.wf.gts.common.beans.TxTransactionGroup;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.common.enums.TransactionStatusEnum;
import com.wf.gts.manage.constant.Constant;
import com.wf.gts.manage.service.TxManagerService;
import com.wufumall.redis.util.JedisUtils;

@Service
public class TxManagerServiceImpl implements TxManagerService{

  @Override
  public Boolean saveTxTransactionGroup(TxTransactionGroup txTransactionGroup) {
    try {
        final String groupId = txTransactionGroup.getId();
        final List<TxTransactionItem> itemList = txTransactionGroup.getItemList();
        if (CollectionUtils.isNotEmpty(itemList)) {
            for (TxTransactionItem item : itemList) {
                JedisUtils.getJedisInstance().execHsetToCache(cacheKey(groupId), item.getTaskKey(),JSON.toJSONString(item));
            }
        }
    } catch (Exception e) {
        return false;
    }
    return true;
  }

  
  
  @Override
  public Boolean addTxTransaction(String txGroupId, TxTransactionItem txTransactionItem) {
      try {
        JedisUtils.getJedisInstance().execHsetToCache(cacheKey(txGroupId), txTransactionItem.getTaskKey(),JSON.toJSONString(txTransactionItem));
      } catch (Exception e) {
          return false;
      }
      return true;
  }

  @Override
  public List<TxTransactionItem> listByTxGroupId(String txGroupId) {
      Map<String,String> entries = JedisUtils.getJedisInstance().execHgetAllToCache(cacheKey(txGroupId));
      return entries.values().stream().map(s->JSON.parseObject(s, TxTransactionItem.class)).collect(Collectors.toList());
  }

  @Override
  public void removeRedisByTxGroupId(String txGroupId) {
      JedisUtils.getJedisInstance().execDelToCache(cacheKey(txGroupId));
  }

  @Override
  public Boolean updateTxTransactionItemStatus(String key, String hashKey, int status) {
      try {
        String item =JedisUtils.getJedisInstance().execHgetToCache(cacheKey(key), hashKey);
        TxTransactionItem txTransactionItem=JSON.parseObject(item, TxTransactionItem.class);
        txTransactionItem.setStatus(status);
        JedisUtils.getJedisInstance().execHsetToCache(cacheKey(key), txTransactionItem.getTaskKey(),JSON.toJSONString(txTransactionItem));
      } catch (BeansException e) {
          return false;
      }
      return true;
  }

  
  
  @Override
  public int findTxTransactionGroupStatus(String txGroupId) {
      try {
          String item =JedisUtils.getJedisInstance().execHgetToCache(cacheKey(txGroupId), txGroupId);
          TxTransactionItem txTransactionItem=JSON.parseObject(item, TxTransactionItem.class);
          return txTransactionItem.getStatus();
      } catch (Exception e) {
          e.printStackTrace();
          return TransactionStatusEnum.ROLLBACK.getCode();
      }
  }


  private String cacheKey(String key) {
      return String.format(Constant.REDIS_PRE_FIX, key);
  }



  @Override
  public Collection<String> listTxGroupId() {
    Collection<String> keys=JedisUtils.getJedisInstance().execKeysToCache(Constant.REDIS_KEYS);
    return keys;
  }

}
