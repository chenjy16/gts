package com.wufumall.example.c;

import com.wufumall.SpringBootBaseTestCase;
import com.wufumall.core.dto.result.BaseCommonResult;
import com.wufumall.example.c.facade.ExampleCFacade;
import com.wufumall.example.c.request.ExampleCInsertRequest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author xiong 20171109
 *
 */
@Slf4j
public class ExampleCFacadeTest extends SpringBootBaseTestCase {
	
	@Autowired
	private ExampleCFacade exampleCFacade;
	//新增
    @Test
    public void insertTest() throws Exception{
    	ExampleCInsertRequest request =  new ExampleCInsertRequest();
    	request.setNumber("23");
    	request.setStatus(1);
    	request.setType(1);
    	BaseCommonResult result = exampleCFacade.insert(request);
    	System.out.println("返回状态码：" + result.getCode() + "返回结果：" + result.getMsg());
        
    } 
    
    
}
