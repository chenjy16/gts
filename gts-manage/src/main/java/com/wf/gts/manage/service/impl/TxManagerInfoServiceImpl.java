package com.wf.gts.manage.service.impl;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.eureka.EurekaServerContextHolder;
import com.wf.gts.common.SocketManager;
import com.wf.gts.common.entity.TxManagerServer;
import com.wf.gts.common.entity.TxManagerServiceDTO;
import com.wf.gts.manage.domain.NettyParam;
import com.wf.gts.manage.entity.TxManagerInfo;
import com.wf.gts.manage.service.DiscoveryService;
import com.wf.gts.manage.service.TxManagerInfoService;

@Service("txManagerInfoService")
public class TxManagerInfoServiceImpl implements TxManagerInfoService {

    @Autowired(required = false)
    private DiscoveryService discoveryService;

    @Autowired
    private NettyParam nettyParam;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${redis_save_max_time}")
    private int redis_save_max_time;

    @Value("${transaction_wait_max_time}")
    private int transaction_wait_max_time;

    @Value("${tx.manager.ip}")
    private String ip;

    @Override
    public TxManagerServer findTxManagerServer() {
        final List<String> manageService = findManageService();
        
        if (CollectionUtils.isNotEmpty(manageService)) {
          
            final List<TxManagerInfo> txManagerInfos = manageService.stream().map(url ->
            restTemplate.getForObject(url + "/tx/manager/findTxManagerInfo", TxManagerInfo.class))
            .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(txManagerInfos)) {
              
                //获取连接数最多的服务  想要把所有的业务长连接，连接到同一个tm，但是又不能超过最大的连接
                final Optional<TxManagerInfo> txManagerInfoOptional =
                        txManagerInfos.stream().filter(Objects::nonNull)
                                .filter(info -> info.getNowConnection() < info.getMaxConnection())
                                .sorted(Comparator.comparingInt(TxManagerInfo::getNowConnection).reversed())
                                .findFirst();
                
                if (txManagerInfoOptional.isPresent()) {
                    final TxManagerInfo txManagerInfo = txManagerInfoOptional.get();
                    TxManagerServer txManagerServer = new TxManagerServer();
                    txManagerServer.setHost(txManagerInfo.getIp());
                    txManagerServer.setPort(txManagerInfo.getPort());
                    return txManagerServer;
                }
            }
        }
        return null;
    }

    @Override
    public TxManagerInfo findTxManagerInfo() {
        TxManagerInfo txManagerInfo = new TxManagerInfo();
        txManagerInfo.setIp(ip);
        txManagerInfo.setPort(nettyParam.getPort());
        txManagerInfo.setMaxConnection(SocketManager.getInstance().getMaxConnection());
        txManagerInfo.setNowConnection(SocketManager.getInstance().getNowConnection());
        txManagerInfo.setTransactionWaitMaxTime(transaction_wait_max_time);
        txManagerInfo.setRedisSaveMaxTime(redis_save_max_time);
        txManagerInfo.setClusterInfoList(findManageService());
        return txManagerInfo;
    }

    
    
    
    
    @Override
    public List<TxManagerServiceDTO> loadTxManagerService() {
        List<InstanceInfo> instanceInfoList = discoveryService.getManageServiceInstances();
        return instanceInfoList.stream().map(instanceInfo -> {
            TxManagerServiceDTO dto = new TxManagerServiceDTO();
            dto.setAppName(instanceInfo.getAppName());
            dto.setInstanceId(instanceInfo.getInstanceId());
            dto.setHomepageUrl(instanceInfo.getHomePageUrl());
            return dto;
        }).collect(Collectors.toList());
    }

    
    
    /**
     * 功能描述: 获取事务管理服务地址
     * @author: chenjy
     * @date: 2017年9月18日 下午5:46:26 
     * @return
     */
    private List<String> findManageService() {
        final List<InstanceInfo> configServiceInstances = discoveryService.getManageServiceInstances();
        return configServiceInstances.stream().map(InstanceInfo::getHomePageUrl).collect(Collectors.toList());
    }
    
    
    
    
}
