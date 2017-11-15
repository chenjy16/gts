package com.wufumall.example.a.controller;

import com.wufumall.core.dto.result.BaseCommonResult;
import com.wufumall.example.a.model.TxExampleA;
import com.wufumall.example.a.service.ExampleAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    	BaseCommonResult result = new BaseCommonResult();
    	try{
    		result = exampleAService.insert(request);
    	}catch(Exception e){
    		result.setCode(99999);
    		result.setMsg(e.getMessage());
    		e.printStackTrace();
    	}
        return result;
    }
    
    @RequestMapping(value = "/insertAll",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public BaseCommonResult insertAll(@RequestBody TxExampleA request) throws Exception{
    	BaseCommonResult result = new BaseCommonResult();
    	try{
    		result = exampleAService.insertAll(request);
    	}catch(Exception e){
    		result.setCode(99999);
    		result.setMsg(e.getMessage());
    		e.printStackTrace();
    	}
      return result;
    }
    
    @RequestMapping(value = "/testBFail",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public BaseCommonResult testBFail(@RequestBody TxExampleA request) throws Exception{
    	BaseCommonResult result = new BaseCommonResult();
    	try{
    		result = exampleAService.testBFail(request);
    	}catch(Exception e){
    		result.setCode(99999);
    		result.setMsg(e.getMessage());
    		e.printStackTrace();
    	}
        return result;
    }
    
    @RequestMapping(value = "/testBTimeout",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public BaseCommonResult testBTimeout(@RequestBody TxExampleA request) throws Exception{
    	BaseCommonResult result = new BaseCommonResult();
    	try{
    		result = exampleAService.testBTimeout(request);
    	}catch(Exception e){
    		result.setCode(99999);
    		result.setMsg(e.getMessage());
    		e.printStackTrace();
    	}
        return result;
    }
    
    
    @RequestMapping(value = "/testCFail",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public BaseCommonResult testCFail(@RequestBody TxExampleA request) throws Exception{
    	BaseCommonResult result = new BaseCommonResult();
    	try{
    		result = exampleAService.testCFail(request);
    	}catch(Exception e){
    		result.setCode(99999);
    		result.setMsg(e.getMessage());
    		e.printStackTrace();
    	}
        return result;
    }
    
    @RequestMapping(value = "/testCTimeout",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public BaseCommonResult testCTimeout(@RequestBody TxExampleA request) throws Exception{
    	BaseCommonResult result = new BaseCommonResult();
    	try{
    		result = exampleAService.testCTimeout(request);
    	}catch(Exception e){
    		result.setCode(99999);
    		result.setMsg(e.getMessage());
    		e.printStackTrace();
    	}
        return result;
    }
}
