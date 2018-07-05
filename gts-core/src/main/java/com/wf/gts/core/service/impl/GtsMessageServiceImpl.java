package com.wf.gts.core.service.impl;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wf.gts.common.beans.TransGroup;
import com.wf.gts.common.beans.TransItem;
import com.wf.gts.core.client.ClientInstance;
import com.wf.gts.core.exception.GtsManageException;
import com.wf.gts.core.lb.ConsistentHashLoadBalancer;
import com.wf.gts.core.service.GtsMessageService;
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
import com.wf.gts.remoting.protocol.GtsManageLiveAddr;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RemotingSerializable;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;

/**
 * netty与txManager进行消息通信
 */
@Service
public class GtsMessageServiceImpl implements GtsMessageService {
  
    @Autowired
    private  ClientInstance clientInstance;
    
    
    
    /**
     * 保存事务组 在事务发起方的时候进行调用
     * @param txTransactionGroup 事务组
     * @return true 成功 false 失败
     * @throws Throwable 
     */
    @Override
    public Boolean saveTxTransactionGroup(TransGroup tx,long timeout) throws Throwable {
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.SAVE_TRANSGROUP, null);
        byte[] body = RemotingSerializable.encode(tx);
        request.setBody(body);
        //一致性哈希
        GtsManageLiveAddr gtsManageLiveAddr=ConsistentHashLoadBalancer.doSelect(clientInstance.getConfig().getClientIP(), clientInstance.getLiveManageRef().get().getGtsManageLiveAddrs());
        RemotingCommand res=clientInstance.getClientAPIImpl().sendMessageSync(gtsManageLiveAddr.getGtsManageAddr(), timeout, request);
        
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
    public Boolean addTxTransaction(String txGroupId, TransItem txTransactionItem,long timeout) throws Throwable {
        AddTransRequestHeader  header=new AddTransRequestHeader();
        header.setTxGroupId(txGroupId);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.ADD_TRANS, header);
        byte[] body = RemotingSerializable.encode(txTransactionItem);
        request.setBody(body);
        GtsManageLiveAddr gtsManageLiveAddr=ConsistentHashLoadBalancer.doSelect(clientInstance.getConfig().getClientIP(), clientInstance.getLiveManageRef().get().getGtsManageLiveAddrs());
        RemotingCommand res=clientInstance.getClientAPIImpl().sendMessageSync(gtsManageLiveAddr.getGtsManageAddr(), timeout, request);
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
      GtsManageLiveAddr gtsManageLiveAddr=ConsistentHashLoadBalancer.doSelect(clientInstance.getConfig().getClientIP(), clientInstance.getLiveManageRef().get().getGtsManageLiveAddrs());
      RemotingCommand res=clientInstance.getClientAPIImpl().sendMessageSync(gtsManageLiveAddr.getGtsManageAddr(), timeout, request);
      if(res.getCode()==ResponseCode.SUCCESS){
        FindTransGroupStatusResponseHeader resHeader=(FindTransGroupStatusResponseHeader)res.decodeCommandCustomHeader(FindTransGroupStatusResponseHeader.class);
        return resHeader.getStatus();
      }
      throw new GtsManageException(res.getCode(), res.getRemark());
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
      GtsManageLiveAddr gtsManageLiveAddr=ConsistentHashLoadBalancer.doSelect(clientInstance.getConfig().getClientIP(), clientInstance.getLiveManageRef().get().getGtsManageLiveAddrs());
      RemotingCommand res=clientInstance.getClientAPIImpl().sendMessageSync(gtsManageLiveAddr.getGtsManageAddr(), timeout, request); 
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
        GtsManageLiveAddr gtsManageLiveAddr=ConsistentHashLoadBalancer.doSelect(clientInstance.getConfig().getClientIP(), clientInstance.getLiveManageRef().get().getGtsManageLiveAddrs());
        RemotingCommand res=clientInstance.getClientAPIImpl().sendMessageSync(gtsManageLiveAddr.getGtsManageAddr(), timeout, request); 
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
          TransGroup tx = new TransGroup();
          tx.setId(txGroupId);
          
          TransItem item = new TransItem();
          item.setTaskKey(taskKey);
          item.setStatus(status);
          tx.setItemList(Collections.singletonList(item));
          byte[] body = RemotingSerializable.encode(tx);
          request.setBody(body);
          try {
            GtsManageLiveAddr gtsManageLiveAddr=ConsistentHashLoadBalancer.doSelect(clientInstance.getConfig().getClientIP(), clientInstance.getLiveManageRef().get().getGtsManageLiveAddrs());
            clientInstance.getClientAPIImpl().invokeOnewayImpl(gtsManageLiveAddr.getGtsManageAddr(),request ,3000);
          } catch (RemotingConnectException | RemotingTooMuchRequestException | RemotingTimeoutException
              | RemotingSendRequestException | InterruptedException e) {
            e.printStackTrace();
          }
    }

}
