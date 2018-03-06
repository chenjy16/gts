package com.wufumall.example.a;

import java.util.concurrent.locks.AbstractQueuedLongSynchronizer.ConditionObject;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

import com.alibaba.dubbo.common.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author xiong 20160609
 *
 */
@Slf4j
public class ExampleATest{
	
	
    //testCTimeout
    @Test
    public void awaitTest() throws Exception{
    	List<String> list = new ArrayList<String>();
    	list.add("1232");
    	list.add("");
    	list.add("23ffdfd");
//    	System.out.println(list);
//    	Map<String, String> map = new HashMap<>();
//    	map.put("123", "4565");
//    	map.put("fdgfd", "uiuy");
//    	System.out.println(map);
    	List<String> resultList = new ArrayList<String>();
    	resultList = list.stream().filter(result -> StringUtils.isNotEmpty(result)).map(result -> result).collect(Collectors.toList());
    	System.out.println(resultList);
    } 
    
}
