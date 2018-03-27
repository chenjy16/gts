package com.wf.gts.core.client;
import com.wf.gts.core.client.processor.ClientRemotingProcessor;
import com.wf.gts.core.exception.GtsManageException;
import com.wf.gts.core.exception.GtsClientException;
import com.wf.gts.remoting.InvokeCallback;
import com.wf.gts.remoting.RPCHook;
import com.wf.gts.remoting.RemotingClient;
import com.wf.gts.remoting.exception.RemotingConnectException;
import com.wf.gts.remoting.exception.RemotingException;
import com.wf.gts.remoting.exception.RemotingSendRequestException;
import com.wf.gts.remoting.exception.RemotingTimeoutException;
import com.wf.gts.remoting.exception.RemotingTooMuchRequestException;
import com.wf.gts.remoting.netty.NettyClientConfig;
import com.wf.gts.remoting.netty.NettyRemotingClient;
import com.wf.gts.remoting.protocol.LiveManageInfo;
import com.wf.gts.remoting.protocol.HeartbeatData;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;

public class ClientAPIImpl {
  
  private final RemotingClient remotingClient;
  private final ClientRemotingProcessor clientRemotingProcessor;
  
  
  public ClientAPIImpl(NettyClientConfig nettyClientConfig,RPCHook rpcHook,ClientRemotingProcessor clientRemotingProcessor) {
      this.clientRemotingProcessor=clientRemotingProcessor;
      this.remotingClient = new NettyRemotingClient(nettyClientConfig, null);
      remotingClient.registerRPCHook(rpcHook);
      remotingClient.registerProcessor(RequestCode.ROLLBACK_TRANSGROUP, this.clientRemotingProcessor, null);
      remotingClient.registerProcessor(RequestCode.COMMIT_TRANS, this.clientRemotingProcessor, null);
  }
  
  public void start() {
    remotingClient.start();
  }
  
  public void shutdown() {
    remotingClient.shutdown();
}

  
  /**
   * 功能描述: 获取集群信息
   * @author: chenjy
   * @date: 2018年3月20日 上午11:08:07 
   * @param addr
   * @param timeoutMillis
   * @return
   * @throws InterruptedException
   * @throws RemotingTimeoutException
   * @throws RemotingSendRequestException
   * @throws RemotingConnectException
   * @throws GtsClientException
   */
  public LiveManageInfo getGtsClusterInfo(String addr,long timeoutMillis) throws InterruptedException, RemotingTimeoutException,
      RemotingSendRequestException, RemotingConnectException, GtsClientException {
      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.GET_MANAGE_CLUSTER_INFO, null);
      RemotingCommand response = remotingClient.invokeSync(addr, request, timeoutMillis);
      assert response != null;
      switch (response.getCode()) {
          case ResponseCode.SUCCESS: {
              return LiveManageInfo.decode(response.getBody(), LiveManageInfo.class);
          }
          default:
              break;
      }
      throw new GtsClientException(response.getCode(), response.getRemark());
  }
  
  
  
  /**
   * 功能描述: 同步发送消息
   * @author: chenjy
   * @date: 2018年3月13日 上午10:14:24 
   * @param addr
   * @param brokerName
   * @param timeoutMillis
   * @param request
   * @return
   * @throws RemotingException
   * @throws InterruptedException
   */
  public RemotingCommand sendMessageSync( String addr, long timeoutMillis, RemotingCommand request) throws RemotingException,InterruptedException {
      return remotingClient.invokeSync(addr, request, timeoutMillis);
  }
  
  
  
  /**
   * 功能描述: 异步发送消息
   * @author: chenjy
   * @date:   2018年3月13日 上午10:26:58 
   * @param   addr
   * @param   timeoutMillis
   * @param   request
   * @throws  InterruptedException
   * @throws  RemotingException
   */
  public void sendMessageAsync( String addr, long timeoutMillis, RemotingCommand request, InvokeCallback invokeCallback) throws InterruptedException, RemotingException {
    remotingClient.invokeAsync(addr, request, timeoutMillis, invokeCallback);
  }

  
  
  /**
   * 功能描述: 单向请求
   * @author: chenjy
   * @date: 2018年3月14日 下午5:21:36 
   * @param addr
   * @param request
   * @param timeoutMillis
   * @throws RemotingConnectException
   * @throws RemotingTooMuchRequestException
   * @throws RemotingTimeoutException
   * @throws RemotingSendRequestException
   * @throws InterruptedException
   */
  public void invokeOnewayImpl( String addr,  RemotingCommand request,  long timeoutMillis) throws RemotingConnectException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException, InterruptedException{
    remotingClient.invokeOneway(addr, request, timeoutMillis);
  }
  
  
  
  /**
   * 功能描述: 发送心跳信息
   * @author: chenjy
   * @date: 2018年3月16日 上午9:34:39 
   * @param addr
   * @param heartbeatData
   * @param timeoutMillis
   * @return
   * @throws RemotingException
   * @throws GtsManageException
   * @throws InterruptedException
   */
  public RemotingCommand sendHearbeat(String addr,HeartbeatData heartbeatData,long timeoutMillis) throws RemotingException, GtsManageException, InterruptedException {
      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.HEART_BEAT, null);
      request.setBody(heartbeatData.encode());
      return remotingClient.invokeSync(addr, request, timeoutMillis);
  }
  
}
