package com.wf.gts.manage.service;
import java.util.List;
import com.wf.gts.common.entity.TxManagerServer;
import com.wf.gts.common.entity.TxManagerServiceDTO;
import com.wf.gts.manage.entity.TxManagerInfo;

public interface TxManagerInfoService {

    /**
     * 功能描述: 业务端获取TxManager信息
     * @author: chenjy
     * @date: 2017年9月18日 下午5:43:59 
     * @return
     */
    TxManagerServer findTxManagerServer();


    /**
     * 功能描述: 服务端信息
     * @author: chenjy
     * @date: 2017年9月18日 下午5:44:10 
     * @return
     */
    TxManagerInfo findTxManagerInfo();

    /**
     * 功能描述: 获取eureka上的注册服务
     * @author: chenjy
     * @date: 2017年9月18日 下午5:44:23 
     * @return
     */
    List<TxManagerServiceDTO> loadTxManagerService();




}
