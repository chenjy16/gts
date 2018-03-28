package com.gts.redis.util;

import com.gts.redis.util.spring.AppContextLauncher;
import com.wufumall.core.Constants;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class JedisUtils {

	private static JedisUtils jedisUtils = new JedisUtils();

	private StringRedisTemplate stringRedisTemplate = null;

	private JedisUtils() {
	  stringRedisTemplate = AppContextLauncher.getBean("stringRedisTemplate", StringRedisTemplate.class);
	}

	public static JedisUtils getJedisInstance() {
		return jedisUtils;
	}
	
	
	/**
	 * 功能描述: 得到从缓冲中自增num的值
	 * Author: 陈建宇
	 * Date:   2016年12月14日 上午9:38:45
	 * @param  cacheKey
	 * @param  num
	 * @return Long   
	 */
	public Long execIncrByToCache(final String cacheKey, final int num) {
	  
	  return stringRedisTemplate.execute(new RedisCallback<Long>() {
      @Override
      public Long doInRedis(RedisConnection connection) throws DataAccessException {
        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
        return stringRedisConn.incrBy(cacheKey, num);
      }
    });
	}

	
	/**
	 * 功能描述: 得到从缓冲中自增1的值
	 * Author: 陈建宇
	 * Date:   2016年12月14日 上午9:38:58
	 * @param  cacheKey
	 * @return Long   
	 */
	public Long execIncrToCache(final String cacheKey) {
	  
	  return stringRedisTemplate.execute(new RedisCallback<Long>() {
        @Override
        public Long doInRedis(RedisConnection connection) throws DataAccessException {
          StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
          return stringRedisConn.incr(cacheKey);
        }
    });
	}
	
	
	/**
	 * 功能描述:  得到从缓冲中自减1的值
	 * Author: 陈建宇
	 * Date:   2016年12月29日 下午2:36:16   
	 * return  Long
	 */
	public Long execDecrToCache(final String cacheKey) {
	  
	  return stringRedisTemplate.execute(new RedisCallback<Long>() {
      @Override
      public Long doInRedis(RedisConnection connection) throws DataAccessException {
        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
        return stringRedisConn.decr(cacheKey);
      }
    });
	}

	/**
	 * 功能描述:  得到从缓冲中自减num的值
	 * Author: 陈建宇
	 * Date:   2016年12月29日 下午2:37:04   
	 * return  Long
	 */
	public Long execDecrByToCache(final String cacheKey, final int num) {
	  
	  return stringRedisTemplate.execute(new RedisCallback<Long>() {
      @Override
      public Long doInRedis(RedisConnection connection) throws DataAccessException {
        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
        return stringRedisConn.decrBy(cacheKey,num);
      }
    });		
	}

	
	
	/**
	 * 功能描述: 删除缓存 
	 * Author: 陈建宇
	 * Date:   2016年12月14日 上午9:39:23
	 * @param  cacheKey
	 * @return boolean   
	 */
	public boolean execDelToCache(final String cacheKey) {
	  return stringRedisTemplate.execute(new RedisCallback<Boolean>() {
      @Override
      public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
        return stringRedisConn.del(cacheKey)== Constants.CONSTANT_INT_ZERO ? false : true;
      }
    });   
	}

	
	/**
	 * 功能描述: 存入缓存值 LRU
	 * Author: 陈建宇
	 * Date:   2016年12月14日 上午9:39:38
	 * @param  cacheKey
	 * @param  value    
	 */
	public void execSetToCache(final String cacheKey, final String value) {
	  
  	 stringRedisTemplate.execute(new RedisCallback<Void>() {
        @Override
        public Void doInRedis(RedisConnection connection) throws DataAccessException {
          StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
          stringRedisConn.set(cacheKey,value);
          return null;
        }
    }); 
	}

	/**
	 * 功能描述: 设置key value  seconds
	 * Author: 陈建宇
	 * Date:   2016年12月29日 下午2:34:46   
	 * return  void
	 */
	public void execSetexToCache(final String cacheKey, final int seconds, final String value) {
	  stringRedisTemplate.execute(new RedisCallback<Void>() {
      @Override
      public Void doInRedis(RedisConnection connection) throws DataAccessException {
        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
        stringRedisConn.setEx(cacheKey,seconds,value);
        return null;
      }
    }); 
	}

	/**
	 * 功能描述:  通过key获取value
	 * Author: 陈建宇
	 * Date:   2017年4月25日 下午4:41:44 
	 * param   cacheKey
	 * return  String
	 */
	public String execGetFromCache(final String cacheKey) {
	  
	  return stringRedisTemplate.execute(new RedisCallback<String>() {
      @Override
      public String doInRedis(RedisConnection connection) throws DataAccessException {
        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
        return stringRedisConn.get(cacheKey);
      }
    }); 
	}

	/**
	 * 功能描述: 是否已经缓存
	 * Author: 陈建宇
	 * Date:   2016年12月14日 上午9:40:16
	 * @param  cacheKey
	 * @return Boolean   
	 */
	public Boolean execExistsFromCache(final String cacheKey) {
	  
	  return stringRedisTemplate.execute(new RedisCallback<Boolean>() {
      @Override
      public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
        return stringRedisConn.exists(cacheKey);
      }
    }); 
	}

	/**
	 * 功能描述: 设置过期时间
	 * Author: 陈建宇
	 * Date:   2016年12月14日 上午9:40:29
	 * @param  cacheKey
	 * @param  seconds
	 * @return Long   
	 */
	public Boolean execExpireToCache(final String cacheKey, final int seconds) {
	  return stringRedisTemplate.execute(new RedisCallback<Boolean>() {
      @Override
      public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
        return stringRedisConn.expire(cacheKey,seconds);
      }
    }); 
	}

	
	
	/**
	 * 功能描述:  SETNX actually means "SET if Not eXists"
	 * Author: 陈建宇
	 * Date:   2016年12月29日 下午2:37:49   
	 * return  Long
	 */
	public Boolean execSetnxToCache(final String cacheKey, final String value) {
	  return stringRedisTemplate.execute(new RedisCallback<Boolean>() {
      @Override
      public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
        return stringRedisConn.setNX(cacheKey,value);
      }
	  }); 
	}
	
	
	

	/**
	 * 功能描述: hash操作 存放
	 * Author: 陈建宇
	 * Date:   2016年12月14日 上午9:41:12
	 * @param  cacheKey
	 * @param  hash
	 * @return String   
	 */
	public Void execHmsetToCache(final String cacheKey, final Map<String, String> hash) {
	  return stringRedisTemplate.execute(new RedisCallback<Void>() {
        @Override
        public Void doInRedis(RedisConnection connection) throws DataAccessException {
            StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
            stringRedisConn.hMSet(cacheKey,hash);
           return null;
        }
    }); 
	}



	/**
	 * 功能描述: 返回哈希表 key 中给定域 field 的值。
	 * Author: 陈建宇
	 * Date:   2016年12月14日 上午9:41:40
	 * @param  cacheKey
	 * @param  field
	 * @return String   
	 */
	public String execHgetToCache(final String cacheKey, final String field) {
	  
	  return stringRedisTemplate.execute(new RedisCallback<String>() {
      @Override
      public String doInRedis(RedisConnection connection) throws DataAccessException {
          StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
          return stringRedisConn.hGet(cacheKey, field);
      }
	  }); 
	}

	
	
	/**
	 * 功能描述: 将哈希表 key中的域 field的值设为value
	 * Author: 陈建宇
	 * Date:   2016年12月14日 上午9:42:07
	 * @param  cacheKey
	 * @param  field
	 * @param  value
	 * @return Boolean   
	 */
	public Boolean execHsetToCache(final String cacheKey, final String field, final String value) {
	  return stringRedisTemplate.execute(new RedisCallback<Boolean>() {
      @Override
      public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
         StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
         return stringRedisConn.hSet(cacheKey,field,value);
      }
    }); 
	}

	/**
	 * 功能描述: 返回哈希表 key中,所有的域和值。
	 * Author: 陈建宇
	 * Date:   2016年12月14日 上午9:42:22
	 * @param  cacheKey
	 * @return Map
	 */
	public Map<String, String> execHgetAllToCache(final String cacheKey) {
	  
	  return stringRedisTemplate.execute(new RedisCallback<Map<String, String>>() {
        @Override
        public Map<String, String> doInRedis(RedisConnection connection) throws DataAccessException {
          StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
          return stringRedisConn.hGetAll(cacheKey); 
        }
    }); 
	}

	
	
	
	
	/**
	 * 功能描述: 设置到什么时候过期
	 * Author: 陈建宇
	 * Date:   2016年12月14日 上午9:42:35
	 * @param  cacheKey
	 * @param  unixTime
	 * @return Boolean   
	 */
	public Boolean execExpireAtTimeToCache(final String cacheKey, final Long unixTime) {
	  return stringRedisTemplate.execute(new RedisCallback<Boolean>() {
      @Override
      public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
        return stringRedisConn.expireAt(cacheKey,unixTime);
      }
    }); 
	}
  	
	 /**
	  * 功能描述: 删除和获取列表中的第一个元素，或阻塞直到有可用
	  * @author: chenjy
	  * @date: 2017年8月2日 下午3:45:01 
	  * @param timeout
	  * @param msg
	  * @return
	  */
	 public List<String> execBlpopToCache(final int timeout, final String... key) {
	   
  	   return stringRedisTemplate.execute(new RedisCallback<List<String>>() {
  	      @Override
  	      public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
  	        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
  	        return stringRedisConn.bLPop(timeout, key);
  	      }
  	    }); 
	  }
	 
	 
	 /**
	  * 功能描述: list尾部插入所有指定的值
	  * @author: chenjy
	  * @date: 2017年8月2日 下午3:49:19 
	  * @param key
	  * @param strings
	  * @return
	  */
   public Long execRpushToCache(final String key, final String... strings) {
     return stringRedisTemplate.execute(new RedisCallback<Long>() {
       
         @Override
         public Long doInRedis(RedisConnection connection) throws DataAccessException {
            StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
            return stringRedisConn.rPush(key, strings);
         }
     }); 
   }
  
  
  /**
    * 功能描述: 返回存储在key列表的长度
    * @author: chenjy
    * @date: 2017年8月2日 下午4:27:53 
    * @param key
    * @return
    */
   public Long execLlenToCache(final String key) {
       return stringRedisTemplate.execute(new RedisCallback<Long>() {
           @Override
           public Long doInRedis(RedisConnection connection) throws DataAccessException {
             StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
             return stringRedisConn.lLen(key);
           }
       }); 
   }



   /**
    * 功能描述: 根据key模糊匹配
    * @author: chenjy
    * @date: 2017年11月6日 上午9:05:26
    * @param keys
    * @return
    */
   public Collection<String> execKeysToCache(final String keys) {
     return stringRedisTemplate.execute(new RedisCallback<Collection<String>>() {
         @Override
         public Collection<String> doInRedis(RedisConnection connection) throws DataAccessException {
           StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
           return stringRedisConn.keys(keys);
         }
     });
 }



   /**
    * 功能描述: 管道批量操作
    * @author: chenjy
    * @date: 2017年8月28日 下午4:56:41 
    * @param action
    * @return
    */
   public List<Object> executePipeline(RedisCallback<?> action){
     return stringRedisTemplate.executePipelined(action);
   }
   
}
