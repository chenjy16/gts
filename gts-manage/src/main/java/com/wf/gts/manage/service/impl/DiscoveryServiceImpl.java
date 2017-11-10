package com.wf.gts.manage.service.impl;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.wf.gts.manage.service.DiscoveryService;

@Service
public class DiscoveryServiceImpl implements DiscoveryService{
  
  private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryServiceImpl.class);
  private final EurekaClient eurekaClient;

  @Autowired(required = false)
  public DiscoveryServiceImpl(EurekaClient eurekaClient) {
      this.eurekaClient = eurekaClient;
  }
  
  public List<InstanceInfo> getManageServiceInstances() {
      Applications app=eurekaClient.getApplications();
      Application application = eurekaClient.getApplication("gts-manager");
      if (application == null) {
          LOGGER.error("获取注册服务失败！");
      }
      return application != null ? application.getInstances() : new ArrayList<>();
  }

}
