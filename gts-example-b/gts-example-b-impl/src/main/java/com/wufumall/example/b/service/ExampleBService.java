package com.wufumall.example.b.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wufumall.example.b.dao.TxExampleBMapper;
import com.wufumall.example.b.facade.BaseCommonResult;
import com.wufumall.example.b.model.TxExampleB;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 主要作为流程扭转以及异常捕获
 * @author xiong
 *
 */
@Data
@Service
@Slf4j
public class ExampleBService{

	@Autowired
	private TxExampleBMapper exampleBmapper;
	
	public BaseCommonResult insert(TxExampleB request) throws Exception{
		log.info("服务B接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();		
		exampleBmapper.insert(request);
		log.info("服务B接口结束,返回结果为：{}", result);
		return result;
	}

	
}
