package com.wufumall.example.a.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wufumall.example.a.model.TxExampleA;
import com.wufumall.example.a.service.ExampleAService;
import com.wufumall.example.b.facade.BaseCommonResult;

/**
 * 
 * @author xiong 20170606
 *
 */
@RestController
@RequestMapping("/examplea")
public class ExampleAController {

  
    @Autowired
    private ExampleAService exampleAService;

    @RequestMapping(value = "/insert",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public BaseCommonResult insert(@RequestBody TxExampleA request) throws Exception{
    	BaseCommonResult result = exampleAService.insert(request);    	
        return result;
    }
    
    @RequestMapping(value = "/insertAll",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public BaseCommonResult insertAll(@RequestBody TxExampleA request) throws Exception{
    	BaseCommonResult result = exampleAService.insertAll(request);    	
    	return result;
    }
    
    @RequestMapping(value = "/testBFail",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public BaseCommonResult testBFail(@RequestBody TxExampleA request) throws Exception{
    	BaseCommonResult result = exampleAService.testBFail(request);
        return result;
    }
    
    @RequestMapping(value = "/testBTimeout",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public BaseCommonResult testBTimeout(@RequestBody TxExampleA request) throws Exception{
    	BaseCommonResult result = exampleAService.testBTimeout(request);
        return result;
    }
    
    
    @RequestMapping(value = "/testCFail",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public BaseCommonResult testCFail(@RequestBody TxExampleA request) throws Exception{
    	BaseCommonResult result = exampleAService.testCFail(request);
        return result;
    }
    
    @RequestMapping(value = "/testCTimeout",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public BaseCommonResult testCTimeout(@RequestBody TxExampleA request) throws Exception{
    	BaseCommonResult result = exampleAService.testCTimeout(request);
    
        return result;
    }
}
