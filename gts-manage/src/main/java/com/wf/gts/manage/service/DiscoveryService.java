package com.wf.gts.manage.service;

import java.util.List;

import com.netflix.appinfo.InstanceInfo;

public interface DiscoveryService {
  
  /**
   * 功能描述: 得到gts-manage服务实例
   * @author: chenjy
   * @date: 2017年10月18日 下午6:37:58 
   * @return
   */
  public List<InstanceInfo> getManageServiceInstances();

}
