package com.wf.gts.manage.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.wf.gts.manage.BaseTest;
import com.wf.gts.manage.constant.Constant;
import com.wufumall.redis.util.JedisUtils;

public class TxManagerIndexControllerTest extends BaseTest {

  @Test
  public void testFindTxManagerInfo() throws Exception {

    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/tx/list")
        .accept(MediaType.APPLICATION_JSON)); // 请求
    result.andDo(new ResultHandler() {
      @Override
      public void handle(MvcResult result) throws Exception {
        System.out.println(result.getResponse().getContentAsString());
      }
    });
  }


	
	
	@Test
	public void testLoadTxManagerService() throws Exception {
		ResultActions result = mockMvc.perform(post("/gtsManage/tx/manager/loadTxManagerService").accept(MediaType.APPLICATION_JSON)); // 请求
		result.andDo(new ResultHandler() {
			@Override
			public void handle(MvcResult result) throws Exception {
				System.out.println(result.getResponse().getContentAsString());
			}
		});
	}
	
	@Test
	public void delToCache() throws Exception {
		Collection<String> keys=JedisUtils.getJedisInstance().execKeysToCache(Constant.REDIS_KEYS);
	    keys.stream().forEach(key -> {
	        
	        	boolean b = JedisUtils.getJedisInstance().execDelToCache(key);
	    		System.out.println("删除事务组信息：" + b);
	    });
	}

}
