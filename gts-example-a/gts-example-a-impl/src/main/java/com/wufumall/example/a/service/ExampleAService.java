package com.wufumall.example.a.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wf.gts.core.annotation.TxTransaction;
import com.wufumall.core.dto.result.BaseCommonResult;
import com.wufumall.example.a.dao.TxExampleAMapper;
import com.wufumall.example.a.model.TxExampleA;
import com.wufumall.example.b.facade.ExampleBFacade;
import com.wufumall.example.b.request.ExampleBInsertRequest;
import com.wufumall.example.c.facade.ExampleCFacade;
import com.wufumall.example.c.request.ExampleCInsertRequest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 主要作为流程扭转以及异常捕获
 * @author xiong
 *
 */
@Data
@Service
@Slf4j
public class ExampleAService{

	@Autowired
	private TxExampleAMapper exampleAmapper;
	
	@Reference(version="0.0.1")
	private ExampleBFacade exampleBFacade;
	
/*	@Reference(version="0.0.1")
	private ExampleCFacade exampleCFacade;*/
	
	/**
	 * 
	* 功能描述: 正常保存
	* @author: xiongkun
	* @date: 2017年11月13日 下午1:46:24 
	* @param request
	* @return
	* @throws Exception
	 */
	public BaseCommonResult insert(TxExampleA request) throws Exception{
		log.info("主流程接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();		
		exampleAmapper.insert(request);
		
		log.info("主流程接口结束,返回结果为：{}", result);
		return result;
	}
	
	/**
	 * 
	* 功能描述: 正常保存
	* @author: xiongkun
	* @date: 2017年11月13日 下午1:46:24 
	* @param request
	* @return
	* @throws Exception
	 */
	@TxTransaction(serviceTransTimeout=30000)
	public BaseCommonResult insertAll(TxExampleA request) throws Exception{
		log.info("主流程接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();		
		exampleAmapper.insert(request);
		
		ExampleBInsertRequest requestB = new ExampleBInsertRequest();
		requestB.setName("b");
		requestB.setNumber(32l);
		log.info("调用A服务开始,请求参数为：{}",requestB);
		BaseCommonResult resultB = exampleBFacade.insert(requestB);
		log.info("调用A服务结束,返回结果为：{}",resultB);
		
		/*ExampleCInsertRequest requestC = new ExampleCInsertRequest();
		requestC.setNumber("23");
		requestC.setStatus(1);
		requestC.setType(1);
		log.info("调用A服务开始,请求参数为：{}",requestC);
		BaseCommonResult resultC = exampleCFacade.insert(requestC);
		log.info("调用A服务结束,返回结果为：{}",resultC);*/
		
		
		log.info("主流程接口结束,返回结果为：{}", result);
		return result;
	}
	
	/**
	 * 
	* 功能描述: b失败
	* @author: xiongkun
	* @date: 2017年11月13日 下午1:53:10 
	* @param request
	* @return
	 */
	@TxTransaction
	public BaseCommonResult testBFail(TxExampleA request){
		log.info("主流程接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();		
		exampleAmapper.insert(request);
		
		ExampleBInsertRequest requestB = new ExampleBInsertRequest();
		//requestB.setName("testBFail");
		requestB.setNumber(32l);
		log.info("调用B fail服务开始,请求参数为：{}",requestB);
		BaseCommonResult resultB = exampleBFacade.fail(requestB);
		log.info("调用B fail服务结束,返回结果为：{}",resultB);
		
	/*	ExampleCInsertRequest requestC = new ExampleCInsertRequest();
		requestC.setNumber("23");
		requestC.setStatus(1);
		requestC.setType(1);
		log.info("调用C insert服务开始,请求参数为：{}",requestC);
		BaseCommonResult resultC = exampleCFacade.insert(requestC);
		log.info("调用C insert服务结束,返回结果为：{}",resultC);*/
		
		log.info("主流程接口结束,返回结果为：{}", result);
		return result;
	}

	/**
	 * 
	* 功能描述: b超时
	* @author: xiongkun
	* @date: 2017年11月13日 下午1:54:07 
	* @param request
	* @return
	 */
	@TxTransaction
	public BaseCommonResult testBTimeout(TxExampleA request){
		log.info("主流程接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();		
		exampleAmapper.insert(request);
		
		ExampleBInsertRequest requestB = new ExampleBInsertRequest();
		requestB.setName("b");
		requestB.setNumber(32l);
		log.info("调用B timeout服务开始,请求参数为：{}",requestB);
		BaseCommonResult resultB = exampleBFacade.timeout(requestB);
		log.info("调用B timeout服务结束,返回结果为：{}",resultB);
		
	/*	ExampleCInsertRequest requestC = new ExampleCInsertRequest();
		requestC.setNumber("23");
		requestC.setStatus(1);
		requestC.setType(1);
		log.info("调用C insert服务开始,请求参数为：{}",requestC);
		BaseCommonResult resultC = exampleCFacade.insert(requestC);
		log.info("调用C insert服务结束,返回结果为：{}",resultC);*/
		
		log.info("主流程接口结束,返回结果为：{}", result);
		return result;
	}
	
	/**
	 * 
	* 功能描述: c失败
	* @author: xiongkun
	* @date: 2017年11月13日 下午1:53:10 
	* @param request
	* @return
	 */
	@TxTransaction
	public BaseCommonResult testCFail(TxExampleA request){
		log.info("主流程接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();		
		exampleAmapper.insert(request);
		
		ExampleBInsertRequest requestB = new ExampleBInsertRequest();
		requestB.setName("b");
		requestB.setNumber(32l);
		log.info("调用B fail服务开始,请求参数为：{}",requestB);
		BaseCommonResult resultB = exampleBFacade.insert(requestB);
		log.info("调用B fail服务结束,返回结果为：{}",resultB);
		
	/*	ExampleCInsertRequest requestC = new ExampleCInsertRequest();
		//requestC.setNumber("23");
		requestC.setStatus(1);
		requestC.setType(1);
		log.info("调用C insert服务开始,请求参数为：{}",requestC);
		BaseCommonResult resultC = exampleCFacade.fail(requestC);
		log.info("调用C insert服务结束,返回结果为：{}",resultC);*/
		
		log.info("主流程接口结束,返回结果为：{}", result);
		return result;
	}

	/**
	 * 
	* 功能描述: b超时
	* @author: xiongkun
	* @date: 2017年11月13日 下午1:54:07 
	* @param request
	* @return
	 */
	@TxTransaction
	public BaseCommonResult testCTimeout(TxExampleA request){
		log.info("主流程接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();		
		exampleAmapper.insert(request);
		
		ExampleBInsertRequest requestB = new ExampleBInsertRequest();
		requestB.setName("b");
		requestB.setNumber(32l);
		log.info("调用B insert服务开始,请求参数为：{}",requestB);
		BaseCommonResult resultB = exampleBFacade.insert(requestB);
		log.info("调用B insert服务结束,返回结果为：{}",resultB);
		
	/*	ExampleCInsertRequest requestC = new ExampleCInsertRequest();
		requestC.setNumber("23");
		requestC.setStatus(1);
		requestC.setType(1);
		log.info("调用C timeout服务开始,请求参数为：{}",requestC);
		BaseCommonResult resultC = exampleCFacade.timeout(requestC);
		log.info("调用C timeout服务结束,返回结果为：{}",resultC);*/
		
		log.info("主流程接口结束,返回结果为：{}", result);
		return result;
	}
	
}
