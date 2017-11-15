package com.wufumall.example.c.service;

import com.wufumall.core.dto.result.BaseCommonResult;
import com.wufumall.example.c.dao.TxExampleCMapper;
import com.wufumall.example.c.model.TxExampleC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 主要作为流程扭转以及异常捕获
 * @author xiong
 *
 */
@Service
@Slf4j
public class ExampleCService{

	@Autowired
	private TxExampleCMapper exampleCmapper;
	
	public BaseCommonResult insert(TxExampleC request) throws Exception{
		log.info("服务B接口开始,请求参数为：{}",request);
		BaseCommonResult result = new BaseCommonResult();		
		exampleCmapper.insert(request);
		log.info("服务B接口结束,返回结果为：{}", result);
		return result;
	}

	
}
