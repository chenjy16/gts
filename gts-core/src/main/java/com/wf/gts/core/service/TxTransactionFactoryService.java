package com.wf.gts.core.service;
import com.wf.gts.core.bean.TxTransactionInfo;

/**
 * 生成不同实现的 TxTransactionService
 */
@FunctionalInterface
public interface TxTransactionFactoryService<T> {

    /**
     * 返回 实现TxTransactionHandler类的名称
     * @param info
     * @return Class<T>
     * @throws Throwable 抛出异常
     */
    Class<T> factoryOf(TxTransactionInfo info) throws Throwable;
}
