package com.wufumall.example.b;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.wufumall.SpringBootBaseTestCase;
import com.wufumall.example.b.facade.BaseCommonResult;
import com.wufumall.example.b.facade.ExampleBFacade;
import com.wufumall.example.b.request.ExampleBInsertRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author xiong 20160609
 *
 */
@Slf4j
public class ExampleBFacadeTest extends SpringBootBaseTestCase {
	
	@Autowired
	private ExampleBFacade exampleBFacade;
	
	//新增
    @Test
    public void insertTest() throws Exception{
    	ExampleBInsertRequest request =  new ExampleBInsertRequest();
    	request.setName("234cc");
    	request.setNumber(32l);
    	BaseCommonResult result = exampleBFacade.insert(request);
    	System.out.println("返回状态码：" + result.getCode() + "返回结果：" + result.getMsg());
    } 
    
    
}
