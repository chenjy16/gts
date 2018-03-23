package com.wf.gts.common.beans;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 事务方法参数
 */
@Getter
@Setter
public class TransactionInvocation implements Serializable {

    private static final long serialVersionUID = 9209965609104391346L;
    /**
     * 事务执行器
     */
    private Class targetClazz;
    /**
     * 方法
     */
    private String method;
    /**
     * 参数值
     */
    private Object[] argumentValues;
    /**
     * 参数类型
     */
    private Class[] argumentTypes;

    public TransactionInvocation() {
    }

    public TransactionInvocation(Class targetClazz, String method, Object[] argumentValues, Class[] argumentTypes) {
        this.targetClazz = targetClazz;
        this.method = method;
        this.argumentValues = argumentValues;
        this.argumentTypes = argumentTypes;
    }

 
}
