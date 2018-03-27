package com.wf.gts.manage.service.impl;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wf.gts.common.beans.TransGroup;
import com.wf.gts.common.beans.TransItem;
import com.wf.gts.common.enums.TransRoleEnum;
import com.wf.gts.common.enums.TransStatusEnum;
import com.wf.gts.manage.constant.Constant;
import com.wf.gts.manage.service.GtsManagerService;
import com.wufumall.redis.util.JedisUtils;

@Service
public class GtsManagerServiceImpl implements GtsManagerService{

  private static final Logger LOGGER = LoggerFactory.getLogger(GtsManagerServiceImpl.class);
  
  @Override
  public Boolean saveTxTransactionGroup(TransGroup txTransactionGroup) {
    try {
        final String groupId = txTransactionGroup.getId();
        final List<TransItem> itemList = txTransactionGroup.getItemList();
        if (CollectionUtils.isNotEmpty(itemList)) {
            for (TransItem item : itemList) {
                JedisUtils.getJedisInstance().execHsetToCache(cacheKey(groupId), item.getTaskKey(),JSON.toJSONString(item));
            }
        }
    } catch (Exception e) {
        LOGGER.error("保存事务组信息报错:{}",e);
        return false;
    }
    return true;
  }

  
  
  @Override
  public Boolean addTxTransaction(String txGroupId, TransItem txTransactionItem) {
      try {
        JedisUtils.getJedisInstance().execHsetToCache(cacheKey(txGroupId), txTransactionItem.getTaskKey(),JSON.toJSONString(txTransactionItem));
      } catch (Exception e) {
          LOGGER.error("增加事务信息报错:{}",e);
          return false;
      }
      return true;
  }

  @Override
  public List<TransItem> listByTxGroupId(String txGroupId) {
      Map<String,String> entries = JedisUtils.getJedisInstance().execHgetAllToCache(cacheKey(txGroupId));
      return entries.values().stream().map(s->JSON.parseObject(s, TransItem.class)).collect(Collectors.toList());
  }

  @Override
  public void removeRedisByTxGroupId(String txGroupId) {
      JedisUtils.getJedisInstance().execDelToCache(cacheKey(txGroupId));
  }

  @Override
  public Boolean updateTxTransactionItemStatus(String key, String hashKey, int status) {
      try {
        String item =JedisUtils.getJedisInstance().execHgetToCache(cacheKey(key), hashKey);
        TransItem txTransactionItem=JSON.parseObject(item, TransItem.class);
        txTransactionItem.setStatus(status);
        JedisUtils.getJedisInstance().execHsetToCache(cacheKey(key), txTransactionItem.getTaskKey(),JSON.toJSONString(txTransactionItem));
      } catch (BeansException e) {
          LOGGER.error("更新事务状态信息报错:{}",e);
          return false;
      }
      return true;
  }

  
  
  @Override
  public int findTxTransactionGroupStatus(String txGroupId) {
      try {
          String item =JedisUtils.getJedisInstance().execHgetToCache(cacheKey(txGroupId), txGroupId);
          TransItem txTransactionItem=JSON.parseObject(item, TransItem.class);
          return txTransactionItem.getStatus();
      } catch (Exception e) {
          return TransStatusEnum.ROLLBACK.getCode();
      }
  }


  private String cacheKey(String key) {
      return String.format(Constant.REDIS_PRE_FIX, key);
  }



  @Override
  public List<List<TransItem>> listTxTransactionItem() {
    Collection<String> keys=JedisUtils.getJedisInstance().execKeysToCache(Constant.REDIS_KEYS);
    List<List<TransItem>>  lists=Lists.newArrayList();
    keys.stream().forEach(key->{
        final Map<String, String> entries = JedisUtils.getJedisInstance().execHgetAllToCache(key);
        final Collection<String> values = entries.values();
        final List<TransItem> items=values.stream()
            .map(item->JSON.parseObject(item, TransItem.class))
            .collect(Collectors.toList());
        lists.add(items);
     });  
    return lists;
  }
  

  @Override
  public void removeCommitTxGroup() {
    Collection<String> keys=JedisUtils.getJedisInstance().execKeysToCache(Constant.REDIS_KEYS);
    keys.stream().forEach(key -> {
        final Map<String, String> entries = JedisUtils.getJedisInstance().execHgetAllToCache(key);
        final Collection<String> values = entries.values();
        final Optional<TransItem> any =
                values.stream().map(item->JSON.parseObject(item, TransItem.class))
                .filter(item -> item.getRole() == TransRoleEnum.START.getCode()
                     && item.getStatus() == TransStatusEnum.ROLLBACK.getCode())
                .findAny();
        if (any.isPresent()) {
          JedisUtils.getJedisInstance().execDelToCache(key);
        }
    });
  }
  
  /**
   * 
  * 功能描述: <br>
  * @author: xiongkun
  * @date: 2017年11月16日 上午9:48:09
   */
  @Override
  public void removeAllCommit() {
	    Collection<String> keys=JedisUtils.getJedisInstance().execKeysToCache(Constant.REDIS_KEYS);
	    keys.stream().forEach(key -> {
	        final Map<String, String> entries = JedisUtils.getJedisInstance().execHgetAllToCache(key);
	        final Collection<String> values = entries.values();
	        boolean b = values.stream().map(item->JSON.parseObject(item, TransItem.class))
	        		.allMatch(item -> item.getStatus() == TransStatusEnum.COMMIT.getCode());
	        if(b){
	        	JedisUtils.getJedisInstance().execDelToCache(key);
	        }
	    });
	  }
  

}
