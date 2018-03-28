package com.gts.redis.util;
import java.util.Random;

import com.gts.redis.exception.RedisLockException;



public class RedisLockUtil {
	
  	/**加锁标志 **/
    public static final String LOCKED = "TRUE"; 
    /**锁的超时时间（秒），过期删除 **/
    public static final int EXPIRE = 60; 
    /**1毫秒对应的纳秒时间**/
  	public static final long ONE_MILLI_NANOS = 1000000L; 
  	/**默认超时时间（毫秒） **/
    public static final long DEFAULT_TIME_OUT = 2000; 
    /**随机数**/
    public static final Random random = new Random(); 
      
    /**
     * 加锁 保证原子性
     * @param operateLogic 处理逻辑 
     * @param lockCacheKey 锁的cache key
     * @param timeOut 毫秒数    	 获取锁的超时时间 
     * @return 
     */
  	public static <T> T executeSynchOperate(MainOperator<T> operator,
  				String lockCacheKey,long milliTimeout){
  		Boolean locked = false;
  		long startNaros = System.nanoTime();
  		long nanoTimeOut = milliTimeout*1000000L;
  		T resultObj = null;
  		try{
  			while(System.nanoTime()-startNaros < nanoTimeOut){
  				if(JedisUtils.getJedisInstance().execSetnxToCache(lockCacheKey, LOCKED)){
  					JedisUtils.getJedisInstance().execExpireToCache(lockCacheKey, EXPIRE);
  					locked = true;
  					break;
  				}
  				Thread.sleep(30,random.nextInt(500));
  			}
  			resultObj = operator.executeInvokeLogic(locked);
  		}catch(InterruptedException ex){
  			throw new RedisLockException(ex);
  		}finally{
  			if(locked){
  				releaseRedisLock(lockCacheKey);
  			}
  		}
  		return resultObj;
  	}
  	
  	/**
  	 * 操作本身实现的逻辑
  	 * @param <T>
  	 */
  	public abstract interface MainOperator<T>{
  		public abstract T executeInvokeLogic(boolean result);
  	}
  	
  	/**
  	 * 释放锁
  	 * @param cacheKey
  	 */
  	private static void releaseRedisLock(final String cacheKey){
  		JedisUtils.getJedisInstance().execDelToCache(cacheKey);
  	}
  	
}
