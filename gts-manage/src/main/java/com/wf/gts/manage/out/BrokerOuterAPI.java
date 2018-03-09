package com.wf.gts.manage.out;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.manage.exception.MQBrokerException;
import com.wf.gts.manage.protocol.header.RegisterBrokerRequestHeader;
import com.wf.gts.manage.protocol.header.RegisterBrokerResponseHeader;
import com.wf.gts.manage.protocol.header.UnRegisterBrokerRequestHeader;
import com.wf.gts.remoting.RemotingClient;
import com.wf.gts.remoting.exception.RemotingCommandException;
import com.wf.gts.remoting.exception.RemotingConnectException;
import com.wf.gts.remoting.exception.RemotingSendRequestException;
import com.wf.gts.remoting.exception.RemotingTimeoutException;
import com.wf.gts.remoting.exception.RemotingTooMuchRequestException;
import com.wf.gts.remoting.netty.NettyClientConfig;
import com.wf.gts.remoting.netty.NettyRemotingClient;
import com.wf.gts.remoting.protocol.RegisterBrokerBody;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;
import com.wf.gts.remoting.util.KVTable;

public class BrokerOuterAPI {
  
    private static final Logger log = LoggerFactory.getLogger(BrokerOuterAPI.class);
    
    private final RemotingClient remotingClient;
    
    public BrokerOuterAPI(final NettyClientConfig nettyClientConfigs) {
        this.remotingClient = new NettyRemotingClient(nettyClientConfigs);
    }
    
    
    public void start() {
      this.remotingClient.start();
    }
    
    
    public void shutdown() {
      this.remotingClient.shutdown();
    }
    
    /**
     * 功能描述: 本地更新注册中心地址
     * @author: chenjy
     * @date: 2018年3月6日 上午11:31:33 
     * @param addrs
     */
    public void updateNameServerAddressList(final String addrs) {
        List<String> lst = new ArrayList<String>();
        String[] addrArray = addrs.split(";");
        for (String addr : addrArray) {
            lst.add(addr);
        }
        this.remotingClient.updateNameServerAddressList(lst);
    }
    
    
    /**
     * 功能描述: <br>
     * @author: chenjy
     * @date: 2018年3月6日 下午3:01:37 
     * @param clusterName
     * @param brokerAddr
     * @param brokerName
     * @param brokerId
     * @param haServerAddr
     * @param oneway
     * @param timeoutMills
     * @return
     */
    public RegisterBrokerResult registerBrokerAll(
        final String brokerAddr,
        final String brokerName,
        final long brokerId,
        final String haServerAddr,
        final boolean oneway,
        final int timeoutMills) {
      
      
        RegisterBrokerResult registerBrokerResult = null;
        List<String> nameServerAddressList = this.remotingClient.getNameServerAddressList();
        if (nameServerAddressList != null) {
            for (String namesrvAddr : nameServerAddressList) {
                try {
                    RegisterBrokerResult result = this.registerBroker(namesrvAddr,brokerAddr, brokerName, brokerId,
                        haServerAddr, oneway, timeoutMills);
                    if (result != null) {
                        registerBrokerResult = result;
                    }
                    log.info("register broker to name server {} OK", namesrvAddr);
                } catch (Exception e) {
                    log.warn("registerBroker Exception, {}", namesrvAddr, e);
                }
            }
        }
        return registerBrokerResult;
    }
    
    
    
    /**
     * 功能描述: <br>
     * @author: chenjy
     * @date: 2018年3月6日 下午3:01:29 
     * @param namesrvAddr
     * @param clusterName
     * @param brokerAddr
     * @param brokerName
     * @param brokerId
     * @param haServerAddr
     * @param oneway
     * @param timeoutMills
     * @return
     * @throws RemotingCommandException
     * @throws MQBrokerException
     * @throws RemotingConnectException
     * @throws RemotingSendRequestException
     * @throws RemotingTimeoutException
     * @throws InterruptedException
     */
    private RegisterBrokerResult registerBroker(
        final String namesrvAddr,
        final String brokerAddr,
        final String brokerName,
        final long brokerId,
        final String haServerAddr,
        final boolean oneway,
        final int timeoutMills
    ) throws RemotingCommandException, MQBrokerException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
        InterruptedException {
      
        RegisterBrokerRequestHeader requestHeader = new RegisterBrokerRequestHeader();
        requestHeader.setBrokerId(brokerId);
        requestHeader.setBrokerName(brokerName);
        requestHeader.setBrokerAddr(brokerAddr);
        requestHeader.setHaServerAddr(haServerAddr);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.REGISTER_BROKER, requestHeader);
        RegisterBrokerBody requestBody = new RegisterBrokerBody();
        if (oneway) {
            try {
                this.remotingClient.invokeOneway(namesrvAddr, request, timeoutMills);
            } catch (RemotingTooMuchRequestException e) {
                // Ignore
            }
            return null;
        }
        RemotingCommand response = this.remotingClient.invokeSync(namesrvAddr, request, timeoutMills);
        assert response != null;
        switch (response.getCode()) {
            case ResponseCode.SUCCESS: {
                RegisterBrokerResponseHeader responseHeader =
                    (RegisterBrokerResponseHeader) response.decodeCommandCustomHeader(RegisterBrokerResponseHeader.class);
                RegisterBrokerResult result = new RegisterBrokerResult();
                result.setMasterAddr(responseHeader.getMasterAddr());
                result.setHaServerAddr(responseHeader.getHaServerAddr());
                if (response.getBody() != null) {
                    result.setKvTable(KVTable.decode(response.getBody(), KVTable.class));
                }
                return result;
            }
            default:
                break;
        }
        throw new MQBrokerException(response.getCode(), response.getRemark());
    }
    
    public void unregisterBrokerAll(
        final String brokerAddr,
        final String brokerName,
        final long brokerId
    ) {
        List<String> nameServerAddressList = this.remotingClient.getNameServerAddressList();
        if (nameServerAddressList != null) {
            for (String namesrvAddr : nameServerAddressList) {
                try {
                    this.unregisterBroker(namesrvAddr, brokerAddr, brokerName, brokerId);
                    log.info("unregisterBroker OK, NamesrvAddr: {}", namesrvAddr);
                } catch (Exception e) {
                    log.warn("unregisterBroker Exception, {}", namesrvAddr, e);
                }
            }
        }
    }
    
    public void unregisterBroker(
        final String namesrvAddr,
        final String brokerAddr,
        final String brokerName,
        final long brokerId
    ) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, InterruptedException, MQBrokerException {
        UnRegisterBrokerRequestHeader requestHeader = new UnRegisterBrokerRequestHeader();
        requestHeader.setBrokerAddr(brokerAddr);
        requestHeader.setBrokerId(brokerId);
        requestHeader.setBrokerName(brokerName);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.UNREGISTER_BROKER, requestHeader);
        RemotingCommand response = this.remotingClient.invokeSync(namesrvAddr, request, 3000);
        assert response != null;
        switch (response.getCode()) {
            case ResponseCode.SUCCESS: {
                return;
            }
            default:
                break;
        }
        throw new MQBrokerException(response.getCode(), response.getRemark());
    }

}
