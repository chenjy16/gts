package com.wf.gts.core.client;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.alibaba.fastjson.JSON;
import com.wf.gts.common.beans.TransGroup;
import com.wf.gts.common.beans.TransItem;
import com.wf.gts.common.enums.TransRoleEnum;
import com.wf.gts.common.enums.TransStatusEnum;
import com.wf.gts.common.utils.IdWorkerUtils;
import com.wf.gts.core.config.ClientConfig;
import com.wf.gts.remoting.header.AddTransRequestHeader;
import com.wf.gts.remoting.header.FindTransGroupStatusRequestHeader;
import com.wf.gts.remoting.header.FindTransGroupStatusResponseHeader;
import com.wf.gts.remoting.header.PreCommitRequestHeader;
import com.wf.gts.remoting.header.RollBackTransGroupRequestHeader;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RemotingSerializable;
import com.wf.gts.remoting.protocol.RequestCode;



public class ClientInstanceTest {

  
  
    /**
     * 功能描述: 客户端启动和关闭测试
     * @author: chenjy
     * @date: 2018年3月23日 上午9:26:31 
     * @throws Exception
     */
    @Test
    public void testClientStart() throws Exception {
      ClientConfig config=new ClientConfig();
      config.setNamesrvAddr("localhost:8000");
      ClientInstance ins=new ClientInstance();
      ins.start(config);
      Thread.sleep(60L*1000L);
      ins.shutdown();
      System.in.read();
    }
    
    
    /**
     * 功能描述: 客户端保存事务组测试
     * @author: chenjy
     * @date: 2018年3月23日 上午9:26:55 
     * @throws Exception
     */
    @Test
    public void testSaveTxTransactionGroup() throws Exception {
      ClientConfig config=new ClientConfig();
      config.setNamesrvAddr("localhost:8000");
      ClientInstance ins=new ClientInstance();
      ins.start(config);
      //创建事务组信息
      TransGroup txTransactionGroup = new TransGroup();
      txTransactionGroup.setId("test");
      List<TransItem> items = new ArrayList<>(2);
      //tmManager 用redis hash 结构来存储 整个事务组的状态做为hash结构的第一条数据
      TransItem groupItem = new TransItem();
      groupItem.setStatus(TransStatusEnum.BEGIN.getCode());//整个事务组状态为开始
      groupItem.setTransId("test"); //设置事务id为组的id  即为 hashKey
      groupItem.setTaskKey("test");
      groupItem.setRole(TransRoleEnum.START.getCode());
      items.add(groupItem);
      
      TransItem item = new TransItem();
      item.setTaskKey("testtaskey");
      item.setTransId(IdWorkerUtils.getInstance().createUUID());
      item.setRole(TransRoleEnum.START.getCode());
      item.setStatus(TransStatusEnum.BEGIN.getCode());
      item.setTxGroupId("test");
      items.add(item);
      txTransactionGroup.setItemList(items);
      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.SAVE_TRANSGROUP, null);
      byte[] body = RemotingSerializable.encode(txTransactionGroup);
      request.setBody(body);
      RemotingCommand res=ins.getClientAPIImpl().sendMessageSync("localhost:9876", 3000l, request);
      System.out.println(JSON.toJSONString(res));
      System.in.read();
    }
    
    
    
    
    
    /**
     * 功能描述: 客户端添加事务测试
     * @author: chenjy
     * @date: 2018年3月23日 上午9:26:55 
     * @throws Exception
     */
    @Test
    public void testAddTxTransactionGroup() throws Exception {
      ClientConfig config=new ClientConfig();
      config.setNamesrvAddr("localhost:8000");
      ClientInstance ins=new ClientInstance();
      ins.start(config);
      final String waitKey = IdWorkerUtils.getInstance().createTaskKey();
      
      TransItem item = new TransItem();
      item.setTaskKey(waitKey);
      item.setTransId(IdWorkerUtils.getInstance().createUUID());
      item.setStatus(TransStatusEnum.BEGIN.getCode());//开始事务
      item.setRole(TransRoleEnum.ACTOR.getCode());//参与者
      item.setTxGroupId("test");
      
      AddTransRequestHeader  header=new AddTransRequestHeader();
      header.setTxGroupId("test");
      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.ADD_TRANS, header);
      byte[] body = RemotingSerializable.encode(item);
      request.setBody(body);
      RemotingCommand res=ins.getClientAPIImpl().sendMessageSync("localhost:9876", 3000l, request);
      System.out.println(JSON.toJSONString(res));
      System.in.read();
    }
    
    
    
    @Test
    public void testGetTransactionGroupStatus() throws Exception {
      
      ClientConfig config=new ClientConfig();
      config.setNamesrvAddr("localhost:8000");
      ClientInstance ins=new ClientInstance();
      ins.start(config);
      
      FindTransGroupStatusRequestHeader header=new FindTransGroupStatusRequestHeader();
      header.setTxGroupId("test");
      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.FIND_TRANSGROUP_STATUS,header);
      RemotingCommand res=ins.getClientAPIImpl().sendMessageSync("localhost:9876", 3000l, request);
      System.out.println(JSON.toJSONString(res));
      System.in.read();
      
    }
    
    
    
    
    
    /**
     * 功能描述: 提交事务
     * @author: chenjy
     * @date: 2018年3月23日 下午1:40:39 
     * @throws Exception
     */
    @Test
    public void testPreCommitTxTransaction() throws Exception {
      ClientConfig config=new ClientConfig();
      config.setNamesrvAddr("localhost:8000");
      ClientInstance ins=new ClientInstance();
      ins.start(config);
      PreCommitRequestHeader header=new PreCommitRequestHeader();
      header.setTxGroupId("test");
      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.PRE_COMMIT_TRANS, header);
      RemotingCommand res=ins.getClientAPIImpl().sendMessageSync("localhost:9876", 3000l, request);
      System.out.println(JSON.toJSONString(res));
      System.in.read();
    }
    
    
    
    /**
     * 功能描述: 回滚事务
     * @author: chenjy
     * @date: 2018年3月23日 下午1:40:39 
     * @throws Exception
     */
    @Test
    public void testRollBackTxTransaction() throws Exception {
      ClientConfig config=new ClientConfig();
      config.setNamesrvAddr("localhost:8000");
      ClientInstance ins=new ClientInstance();
      ins.start(config);
      
      RollBackTransGroupRequestHeader  header=new RollBackTransGroupRequestHeader();
      header.setTxGroupId("test");
      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.ROLLBACK_TRANSGROUP, header);
      RemotingCommand res=ins.getClientAPIImpl().sendMessageSync("localhost:9876", 3000l, request);
      System.out.println(JSON.toJSONString(res));
      System.in.read();
    }
 
}