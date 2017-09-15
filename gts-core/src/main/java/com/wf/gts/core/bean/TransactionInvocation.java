package com.wf.gts.core.bean;

import java.io.Serializable;

/**
 * 事务补偿方法参数
 */
public class TransactionInvocation implements Serializable {
    private static final long serialVersionUID = 7722060715819141844L;
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

    public Class getTargetClazz() {
        return targetClazz;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getArgumentValues() {
        return argumentValues;
    }

    public Class[] getArgumentTypes() {
        return argumentTypes;
    }
}
