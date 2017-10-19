package com.wf.gts.manage.tcc.service;

import com.wf.gts.manage.entity.TccRequest;

public interface CoordinateService {
  
  
  /**
   * 功能描述: 确认预留资源
   * @author: chenjy
   * @date:   2017年9月21日 下午1:33:15 
   * @param   request
   */
  public void confirm(TccRequest request);
  
  
  /**
   * 功能描述: 撤销预留资源
   * @author: chenjy
   * @date:   2017年9月21日 下午1:33:21 
   * @param   request
   */
  public void cancel(TccRequest request);

}
