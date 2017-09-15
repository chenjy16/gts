package com.wf.gts.core.service;

import com.wf.gts.core.bean.TransactionRecover;
import com.wf.gts.core.config.TxConfig;

/**
 * 本地补偿的方法
 */
public interface TxCompensationService {

    void compensate();


    /**
     * 启动本地补偿事务，根据配置是否进行补偿
     */
    void start(TxConfig txConfig) throws Exception;

    /**
     * 保存补偿事务信息
     *
     * @param transactionRecover 实体对象
     * @return 主键id
     */
    String save(TransactionRecover transactionRecover);


    /**
     * 删除补偿事务信息
     *
     * @param id 主键id
     * @return true成功 false 失败
     */
    boolean remove(String id);


    /**
     * 更新
     *
     * @param transactionRecover 实体对象
     */
    void update(TransactionRecover transactionRecover);

    /**
     * 提交补偿操作
     *
     * @param txCompensationAction 补偿命令
     */
    Boolean submit(TxCompensationAction txCompensationAction);
}
