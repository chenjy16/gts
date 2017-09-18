package com.wf.gts.core.concurrent;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;

/**
 * 采用google cache 来缓存task类 (放弃concurrentHashMap)
 */
public class BlockTaskHelper {

    private static final int MAX_COUNT = 10000;

    private static final BlockTaskHelper BLOCK_TASK_HELPER = new BlockTaskHelper();

    private BlockTaskHelper() {

    }

    private static final LoadingCache<String, BlockTask> cache = CacheBuilder.newBuilder()
      .maximumWeight(MAX_COUNT)
      .weigher((Weigher<String, BlockTask>) (string, BlockTask) -> getSize())
      
      .build(new CacheLoader<String, BlockTask>() {
          @Override
          public BlockTask load(String key) throws Exception {
              return createTask(key);
          }
    });


    public static BlockTaskHelper getInstance() {
        return BLOCK_TASK_HELPER;
    }

    
    private static int getSize() {
        if (cache == null) {
            return 0;
        }
        return (int) cache.size();
    }


    private  static BlockTask createTask(String key) {
        BlockTask task = new BlockTask();
        task.setKey(key);
        return task;
    }


    /**
     * 获取task
     * @param key 需要获取的key
     */
    public BlockTask getTask(String key) {
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }


    public void removeByKey(String key) {
        if (StringUtils.isNotEmpty(key)) {
            cache.invalidate(key);
        }
    }


}
