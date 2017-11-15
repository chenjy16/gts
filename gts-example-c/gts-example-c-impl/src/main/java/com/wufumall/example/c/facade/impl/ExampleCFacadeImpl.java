package com.wufumall.example.c.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wf.gts.core.annotation.TxTransaction;
import com.wufumall.core.dto.result.BaseCommonResult;
import com.wufumall.example.c.dao.TxExampleCMapper;
import com.wufumall.example.c.facade.ExampleCFacade;
import com.wufumall.example.c.model.TxExampleC;
import com.wufumall.example.c.request.ExampleCInsertRequest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 主要作为流程扭转以及异常捕获
 * @author xiong
 *
 */
@Data
@Service(version = "0.0.1")
@Slf4j
public class ExampleCFacadeImpl implements ExampleCFacade{

	@Autowired
	private TxExampleCMapper exampleBmapper;

	@Override
	@TxTransaction
	public BaseCommonResult insert(ExampleCInsertRequest request) {
		log.info("服务C insert接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();	
		TxExampleC ec = new TxExampleC();
		ec.setNumber(request.getNumber());
		ec.setType(1);
		ec.setStatus(1);
		ec.setCreateTime(new Date());
		
		exampleBmapper.insert(ec);
		log.info("服务C insert接口结束,返回结果为：{}", result);
		return result;
	}

	@Override
	@TxTransaction
	public BaseCommonResult fail(ExampleCInsertRequest request) {
		log.info("服务C fail接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();	
		TxExampleC ec = new TxExampleC();
		ec.setNumber(request.getNumber());
		ec.setType(1);
		ec.setStatus(1);
		ec.setCreateTime(new Date());
		
		exampleBmapper.insert(ec);
		log.info("服务C fail接口结束,返回结果为：{}", result);
		return result;
	}

	@Override
	@TxTransaction
	public BaseCommonResult timeout(ExampleCInsertRequest request) {
		log.info("服务C fail接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();	
		TxExampleC ec = new TxExampleC();
		ec.setNumber(request.getNumber());
		ec.setType(1);
		ec.setStatus(1);
		ec.setCreateTime(new Date());
		
		exampleBmapper.insert(ec);
		
		//模拟超时
        try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("服务C fail接口结束,返回结果为：{}", result);
		return result;
	}

	
}
