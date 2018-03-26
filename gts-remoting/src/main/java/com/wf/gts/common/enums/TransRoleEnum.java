package com.wf.gts.common.enums;


/**
 * 事务角色枚举
 */
public enum TransRoleEnum {
    START(0, "发起者"),
    ACTOR(1, "参与者"),
    ;

    private int code;
    private String desc;

    TransRoleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
