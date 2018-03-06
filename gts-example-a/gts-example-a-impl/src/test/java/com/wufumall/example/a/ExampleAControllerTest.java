package com.wufumall.example.a;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.wufumall.SpringBootBaseTestCase;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import com.alibaba.fastjson.JSON;
import com.wufumall.example.a.model.TxExampleA;
import lombok.extern.slf4j.Slf4j;



/**
 * @author xiong 20160609
 */
@Slf4j
public class ExampleAControllerTest extends SpringBootBaseTestCase {
	
	//新增
    @Test
    public void insertTest() throws Exception{
    	TxExampleA request = new TxExampleA();
    	request.setName("3rew");
        ResultActions result = mockMvc.perform(post("/examplea/insert")
                .contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(request))
                .accept(MediaType.APPLICATION_JSON)) //执行请求
                .andExpect(status().isOk()); //请求
        
        result.andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                log.info("新增消息返回信息为：{}" , result.getResponse().getContentAsString());
            }
        });
    } 
    
    
    //正常新增
    @Test
    public void insertAllTest() throws Exception{
      
    	  TxExampleA request = new TxExampleA();
    	  request.setName("3rew");
        ResultActions result = mockMvc.perform(post("/examplea/insertAll")
                .contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(request))
                .accept(MediaType.APPLICATION_JSON)) //执行请求
                .andExpect(status().isOk()); //请求
        result.andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                log.info("insertAll返回信息为：{}" , result.getResponse().getContentAsString());
            }
        });
        
        System.in.read();
    } 
    
    
    //testBFail
    @Test
    public void testBFailTest() throws Exception{
    	TxExampleA request = new TxExampleA();
    	request.setName("testBFail");
        ResultActions result = mockMvc.perform(post("/examplea/testBFail")
                .contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(request))
                .accept(MediaType.APPLICATION_JSON)) //执行请求
                .andExpect(status().isOk()); //请求
        
        result.andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                log.info("testBFail返回信息为：{}" , result.getResponse().getContentAsString());
            }
        });
        System.in.read();
    } 
    
    //testBTimeout
    @Test
    public void testBTimeoutTest() throws Exception{
    	TxExampleA request = new TxExampleA();
    	request.setName("testBTimeout");
        ResultActions result = mockMvc.perform(post("/examplea/testBTimeout")
                .contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(request))
                .accept(MediaType.APPLICATION_JSON)) //执行请求
                .andExpect(status().isOk()); //请求
        result.andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                log.info("testBTimeout返回信息为：{}" , result.getResponse().getContentAsString());
            }
        });
    } 
    
    //testCFail
    @Test
    public void testCFailTest() throws Exception{
    	TxExampleA request = new TxExampleA();
    	request.setName("testCFail");
        ResultActions result = mockMvc.perform(post("/examplea/testCFail")
                .contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(request))
                .accept(MediaType.APPLICATION_JSON)) //执行请求
                .andExpect(status().isOk()); //请求
        result.andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                log.info("testCFail返回信息为：{}" , result.getResponse().getContentAsString());
            }
        });
       // System.in.read();
    } 
    
    //testCTimeout
    @Test
    public void testCTimeoutTest() throws Exception{
    	TxExampleA request = new TxExampleA();
    	request.setName("testCTimeout");
        ResultActions result = mockMvc.perform(post("/examplea/testCTimeout")
                .contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(request))
                .accept(MediaType.APPLICATION_JSON)) //执行请求
                .andExpect(status().isOk()); //请求
        result.andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                log.info("testCTimeout返回信息为：{}" , result.getResponse().getContentAsString());
            }
        });
    } 
    
}
