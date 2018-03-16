package com.wf.gts.core.client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wf.gts.core.exception.MQBrokerException;
import com.wf.gts.core.exception.MQClientException;
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
import com.wf.gts.remoting.protocol.ClusterInfo;
import com.wf.gts.remoting.protocol.HeartbeatData;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;

public class MQClientAPIImpl {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MQClientAPIImpl.class);
  
  
  private final RemotingClient remotingClient;
  private final ClientRemotingProcessor clientRemotingProcessor;
  
  public MQClientAPIImpl(NettyClientConfig nettyClientConfig,RPCHook rpcHook,ClientRemotingProcessor clientRemotingProcessor) {
      this.clientRemotingProcessor=clientRemotingProcessor;
      this.remotingClient = new NettyRemotingClient(nettyClientConfig, null);
      this.remotingClient.registerRPCHook(rpcHook);
      this.remotingClient.registerProcessor(RequestCode.ROLLBACK_TRANSGROUP, this.clientRemotingProcessor, null);
  }
  
  public void start() {
    this.remotingClient.start();
  }
  
  public void shutdown() {
    this.remotingClient.shutdown();
}

 
  public ClusterInfo getBrokerClusterInfo(String addr,
      final long timeoutMillis) throws InterruptedException, RemotingTimeoutException,
      RemotingSendRequestException, RemotingConnectException, MQClientException {
      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.GET_BROKER_CLUSTER_INFO, null);
      RemotingCommand response = this.remotingClient.invokeSync(addr, request, timeoutMillis);
      assert response != null;
      switch (response.getCode()) {
          case ResponseCode.SUCCESS: {
              return ClusterInfo.decode(response.getBody(), ClusterInfo.class);
          }
          default:
              break;
      }
      throw new MQClientException(response.getCode(), response.getRemark());
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
  public RemotingCommand sendMessageSync(final String addr,final long timeoutMillis,final RemotingCommand request) throws RemotingException,InterruptedException {
      return this.remotingClient.invokeSync(addr, request, timeoutMillis);
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
  public void sendMessageAsync(final String addr,final long timeoutMillis,final RemotingCommand request,final InvokeCallback invokeCallback) throws InterruptedException, RemotingException {
    this.remotingClient.invokeAsync(addr, request, timeoutMillis, invokeCallback);
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
  public void invokeOnewayImpl(final String addr, final RemotingCommand request, final long timeoutMillis) throws RemotingConnectException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException, InterruptedException{
    this.remotingClient.invokeOneway(addr, request, timeoutMillis);
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
   * @throws MQBrokerException
   * @throws InterruptedException
   */
  public int sendHearbeat(String addr,HeartbeatData heartbeatData,long timeoutMillis) throws RemotingException, MQBrokerException, InterruptedException {
      RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.HEART_BEAT, null);
      request.setBody(heartbeatData.encode());
      RemotingCommand response = this.remotingClient.invokeSync(addr, request, timeoutMillis);
      assert response != null;
      switch (response.getCode()) {
          case ResponseCode.SUCCESS: {
              return response.getVersion();
          }
          default:
              break;
      }
      throw new MQBrokerException(response.getCode(), response.getRemark());
  }
  
}
