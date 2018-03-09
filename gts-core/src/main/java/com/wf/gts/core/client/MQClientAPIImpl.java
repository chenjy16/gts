package com.wf.gts.core.client;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import com.wf.gts.core.exception.MQClientException;
import com.wf.gts.remoting.RPCHook;
import com.wf.gts.remoting.RemotingClient;
import com.wf.gts.remoting.exception.RemotingConnectException;
import com.wf.gts.remoting.exception.RemotingSendRequestException;
import com.wf.gts.remoting.exception.RemotingTimeoutException;
import com.wf.gts.remoting.netty.NettyClientConfig;
import com.wf.gts.remoting.netty.NettyRemotingClient;
import com.wf.gts.remoting.protocol.ClusterInfo;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;

public class MQClientAPIImpl {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MQClientAPIImpl.class);
  
  private final RemotingClient remotingClient;
  
  
  public MQClientAPIImpl(final NettyClientConfig nettyClientConfig,RPCHook rpcHook) {
      this.remotingClient = new NettyRemotingClient(nettyClientConfig, null);
      this.remotingClient.registerRPCHook(rpcHook);
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

}
