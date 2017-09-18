package com.wf.gts.manage.service;

import java.util.List;

import com.netflix.appinfo.InstanceInfo;

public interface DiscoveryService {
  
  public List<InstanceInfo> getConfigServiceInstances();

}
