package com.wufumall.example.b.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wf.gts.core.annotation.GtsTransaction;
import com.wufumall.core.dto.result.BaseCommonResult;
import com.wufumall.example.b.dao.TxExampleBMapper;
import com.wufumall.example.b.facade.ExampleBFacade;
import com.wufumall.example.b.model.TxExampleB;
import com.wufumall.example.b.request.ExampleBInsertRequest;

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
public class ExampleBFacadeImpl implements ExampleBFacade{

	@Autowired
	private TxExampleBMapper exampleBmapper;

	@Override
	@GtsTransaction
	public BaseCommonResult insert(ExampleBInsertRequest request) {
		log.info("服务B insert接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();	
		TxExampleB eb = new TxExampleB();
		eb.setName(request.getName());
		eb.setNumber(request.getNumber());
		eb.setCreateTime(new Date());
		exampleBmapper.insert(eb);
		log.info("服务B insert接口结束,返回结果为：{}", result);
		return result;
	}

	@Override
	@GtsTransaction
	public BaseCommonResult fail(ExampleBInsertRequest request) {
		log.info("服务B fail接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();	
		TxExampleB eb = new TxExampleB();
		eb.setName(request.getName());
		eb.setNumber(request.getNumber());
		eb.setCreateTime(new Date());
		exampleBmapper.insert(eb);
		log.info("服务B fail接口结束,返回结果为：{}", result);
		return result;
	}

	@Override
	@GtsTransaction
	public BaseCommonResult timeout(ExampleBInsertRequest request) {
		log.info("服务B timeout接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();	
		TxExampleB eb = new TxExampleB();
		eb.setName(request.getName());
		eb.setNumber(request.getNumber());
		eb.setCreateTime(new Date());
		exampleBmapper.insert(eb);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("服务B timeout接口结束,返回结果为：{}", result);
		return result;
	}

	
}
