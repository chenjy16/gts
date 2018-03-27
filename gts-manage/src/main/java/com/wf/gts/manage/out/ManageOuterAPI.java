package com.wf.gts.manage.out;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wf.gts.manage.exception.GtsManageException;
import com.wf.gts.remoting.RemotingClient;
import com.wf.gts.remoting.exception.RemotingCommandException;
import com.wf.gts.remoting.exception.RemotingConnectException;
import com.wf.gts.remoting.exception.RemotingSendRequestException;
import com.wf.gts.remoting.exception.RemotingTimeoutException;
import com.wf.gts.remoting.header.RegisterBrokerRequestHeader;
import com.wf.gts.remoting.header.RegisterBrokerResponseHeader;
import com.wf.gts.remoting.header.UnRegisterBrokerRequestHeader;
import com.wf.gts.remoting.netty.NettyClientConfig;
import com.wf.gts.remoting.netty.NettyRemotingClient;
import com.wf.gts.remoting.protocol.RegisterBrokerResult;
import com.wf.gts.remoting.protocol.RemotingCommand;
import com.wf.gts.remoting.protocol.RequestCode;
import com.wf.gts.remoting.protocol.ResponseCode;


public class ManageOuterAPI {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageOuterAPI.class);
    
    private final RemotingClient remotingClient;
    
    public ManageOuterAPI(NettyClientConfig nettyClientConfigs) {
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
    public void updateNameServerAddressList(String addrs) {
        List<String> lst = new ArrayList<String>();
        String[] addrArray = addrs.split(";");
        for (String addr : addrArray) {
            lst.add(addr);
        }
        this.remotingClient.updateNameServerAddressList(lst);
    }
    
    
    /**
     * 功能描述:注册manage地址
     * @author: chenjy
     * @date: 2018年3月21日 下午5:22:32 
     * @param manageAddr
     * @param manageName
     * @param manageId
     * @param timeoutMills
     * @return
     */
    public RegisterBrokerResult registerManageAll(String manageAddr,String manageName,long manageId,int timeoutMills) {
        RegisterBrokerResult registerBrokerResult = null;
        
        List<String> nameServerAddressList = this.remotingClient.getNameServerAddressList();
        if (nameServerAddressList != null) {
            for (String namesrvAddr : nameServerAddressList) {
                try {
                    RegisterBrokerResult result = this.registerManage(namesrvAddr,manageAddr, manageName, manageId,
                      timeoutMills);
                    if (result != null) {
                        registerBrokerResult = result;
                    }
                    LOGGER.info("register broker to name server {} OK", namesrvAddr);
                } catch (Exception e) {
                  LOGGER.warn("registerBroker Exception, {}", namesrvAddr, e);
                }
            }
        }
        return registerBrokerResult;
    }
    
    
    

    private RegisterBrokerResult registerManage(String namesrvAddr,String manageAddr,String manageName,long manageId,
        int timeoutMills) throws RemotingCommandException, GtsManageException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
        InterruptedException {
      
        RegisterBrokerRequestHeader requestHeader = new RegisterBrokerRequestHeader();
        requestHeader.setBrokerId(manageId);
        requestHeader.setBrokerName(manageName);
        requestHeader.setBrokerAddr(manageAddr);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.REGISTER_MANAGE, requestHeader);
        RemotingCommand response = this.remotingClient.invokeSync(namesrvAddr, request, timeoutMills);
        assert response != null;
        switch (response.getCode()) {
            case ResponseCode.SUCCESS: {
                RegisterBrokerResponseHeader responseHeader =
                    (RegisterBrokerResponseHeader) response.decodeCommandCustomHeader(RegisterBrokerResponseHeader.class);
                RegisterBrokerResult result = new RegisterBrokerResult();
                result.setMasterAddr(responseHeader.getMasterAddr());
                return result;
            }
            default:
                break;
        }
        throw new GtsManageException(response.getCode(), response.getRemark());
    }
    
    
    /**
     * 功能描述: 注销所有的Manage
     * @author: chenjy
     * @date: 2018年3月21日 下午5:22:47 
     * @param manageAddr
     * @param manageName
     * @param manageId
     */
    public void unregisterManageAll(String manageAddr,String manageName,long manageId) {
        List<String> nameServerAddressList = this.remotingClient.getNameServerAddressList();
        if (Objects.nonNull(nameServerAddressList)) {
            nameServerAddressList.stream().forEach(namesrvAddr->{
                try {
                    this.unregisterBroker(namesrvAddr, manageAddr, manageName, manageId);
                    LOGGER.info("unregisterBroker OK, NamesrvAddr: {}", namesrvAddr);
                } catch (Exception e) {
                  LOGGER.warn("unregisterBroker Exception, {}", namesrvAddr, e);
                }
            });
        }
    }
    
    private void unregisterBroker(String namesrvAddr,String manageAddr,String manageName,long manageId) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, InterruptedException, GtsManageException {
        UnRegisterBrokerRequestHeader requestHeader = new UnRegisterBrokerRequestHeader();
        requestHeader.setBrokerAddr(manageAddr);
        requestHeader.setBrokerId(manageId);
        requestHeader.setBrokerName(manageName);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.UNREGISTER_MANAGE, requestHeader);
        RemotingCommand response = this.remotingClient.invokeSync(namesrvAddr, request, 3000);
        assert response != null;
        switch (response.getCode()) {
            case ResponseCode.SUCCESS: {
                return;
            }
            default:
                break;
        }
        throw new GtsManageException(response.getCode(), response.getRemark());
    }

}
