package com.wf.gts.common.enums;




/**
 * 事务角色枚举
 */
public enum TransactionRoleEnum {

    /**
     * Begin transaction status enum.
     */
    START(0, "发起者"),


    /**
     * Fail netty result enum.
     */
    ACTOR(1, "参与者");



    private int code;

    private String desc;

    TransactionRoleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    /**
     * Gets code.
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets code.
     * @param code the code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Gets desc.
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     * @param desc the desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
