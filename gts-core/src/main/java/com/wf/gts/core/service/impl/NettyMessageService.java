package com.wf.gts.core.service.impl;
import java.util.Collections;
import org.springframework.stereotype.Service;
import com.wf.gts.common.beans.TxTransactionGroup;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.core.client.MQClientInstance;
import com.wf.gts.core.client.MQClientManager;
import com.wf.gts.core.service.TxManagerMessageService;
import com.wf.gts.remoting.exception.RemotingConnectException;
import com.wf.gts.remoting.exception.RemotingException;
import com.wf.gts.remoting.exception.RemotingSendRequestException;
import com.wf.gts.remoting.exception.RemotingTimeoutException;
import com.wf.gts.remoting.exception.RemotingTooMuchRequestException;
import com.wf.gts.remoting.header.AddTransRequestHeader;
import com.wf.gts.remoting.header.FindTransGroupStatusRequestHeader;
import com.wf.gts.remoting.header.FindTransGroupStatusResponseHeader;
import com.wf.gts.remoting.header.PreCommitRequestHeader;
import com.wf.gts.remoting.header.RollBackTransGroupRequestHeader;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RemotingSerializable;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;

/**
 * netty与txManager进行消息通信
 */
@Service
public class NettyMessageService implements TxManagerMessageService {
  
    private final  MQClientInstance  clientInstance;
    
    public NettyMessageService() {
        this.clientInstance = MQClientManager.getInstance().getAndCreateMQClientInstance(null);
    }

    
    /**
     * 保存事务组 在事务发起方的时候进行调用
     * @param txTransactionGroup 事务组
     * @return true 成功 false 失败
     * @throws Throwable 
     */
    @Override
    public Boolean saveTxTransactionGroup(TxTransactionGroup tx,long timeout) throws Throwable {
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.SAVE_TRANSGROUP, null);
        byte[] body = RemotingSerializable.encode(tx);
        request.setBody(body);
        RemotingCommand res=clientInstance.getmQClientAPIImpl().sendMessageSync("", timeout, request);
        if(res.getCode()==ResponseCode.SUCCESS){
          return true;
        }
        return false;
    }

    /**
     * 往事务组添加事务
     * @param txGroupId         事务组id
     * @param txTransactionItem 子事务项
     * @return true 成功   false 失败
     * @throws Throwable 
     */
    @Override
    public Boolean addTxTransaction(String txGroupId, TxTransactionItem txTransactionItem,long timeout) throws Throwable {
        AddTransRequestHeader  header=new AddTransRequestHeader();
        header.setTxGroupId(txGroupId);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.ADD_TRANS, header);
        byte[] body = RemotingSerializable.encode(txTransactionItem);
        request.setBody(body);
        RemotingCommand res=clientInstance.getmQClientAPIImpl().sendMessageSync("", timeout, request);
        if(res.getCode()==ResponseCode.SUCCESS){
          return true;
        }
        return false;
    }

    
    /**
     * 获取事务组状态
     * @param txGroupId 事务组id
     * @return 事务组状态
     * @throws Throwable 
     */
    @Override
    public int findTransactionGroupStatus(String txGroupId,long timeout) throws Throwable {
      FindTransGroupStatusRequestHeader header=new FindTransGroupStatusRequestHeader();
      header.setTxGroupId(txGroupId);
      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.FIND_TRANSGROUP_STATUS,header);
      RemotingCommand res=clientInstance.getmQClientAPIImpl().sendMessageSync("", timeout, request);
      if(res.getCode()==ResponseCode.SUCCESS){
        FindTransGroupStatusResponseHeader resHeader=(FindTransGroupStatusResponseHeader)res.decodeCommandCustomHeader(FindTransGroupStatusResponseHeader.class);
        return resHeader.getStatus();
      }
      throw new Exception();
    }

 

    /**
     * 通知tm 回滚整个事务组
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     * @throws Throwable 
     */
    @Override
    public Boolean rollBackTxTransaction(String txGroupId,long timeout) throws Throwable {
      RollBackTransGroupRequestHeader  header=new RollBackTransGroupRequestHeader();
      header.setTxGroupId(txGroupId);
      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.ROLLBACK_TRANSGROUP, header);
      RemotingCommand res=clientInstance.getmQClientAPIImpl().sendMessageSync("", timeout, request); 
      if(res.getCode()==ResponseCode.SUCCESS){
        return true;
      }
      return false;
    }
    
    
    
    /**
     * 通知tm自身业务已经执行完成，等待提交事务
     * tm 收到后，进行pre_commit  再进行doCommit
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     * @throws Throwable 
     */
    @Override
    public Boolean preCommitTxTransaction(String txGroupId,long timeout) throws Throwable {
      
        PreCommitRequestHeader header=new PreCommitRequestHeader();
        header.setTxGroupId(txGroupId);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.PRE_COMMIT_TRANS, header);
        RemotingCommand res=clientInstance.getmQClientAPIImpl().sendMessageSync("", timeout, request); 
        if(res.getCode()==ResponseCode.SUCCESS){
          return true;
        }
        return false;
    }
   
    
    
    
    /**
     * 异步完成自身事务的提交
     * @param txGroupId 事务组id
     * @param taskKey   子事务的taskKey
     * @param status    状态  
     * @throws RemotingException 
     * @throws InterruptedException 
     */
    @Override
    public void AsyncCompleteCommitTxTransaction(String txGroupId, String taskKey, int status){
          RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.COMMIT_TRANS, null);
          TxTransactionGroup tx = new TxTransactionGroup();
          tx.setId(txGroupId);
          TxTransactionItem item = new TxTransactionItem();
          item.setTaskKey(taskKey);
          item.setStatus(status);
          tx.setItemList(Collections.singletonList(item));
          byte[] body = RemotingSerializable.encode(tx);
          request.setBody(body);
          try {
            clientInstance.getmQClientAPIImpl().invokeOnewayImpl("",request ,0);
          } catch (RemotingConnectException | RemotingTooMuchRequestException | RemotingTimeoutException
              | RemotingSendRequestException | InterruptedException e) {
            e.printStackTrace();
          }
    }


}
