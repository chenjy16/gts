package com.wf.gts.common.enums;


public enum CompensationActionEnum {

    SAVE(0,"保存"),

    DELETE(1,"删除"),

    UPDATE(2,"更新"),

    COMPENSATE(3,"补偿"),

    ;

    private int code;

    private String desc;

    CompensationActionEnum(int code,String desc){
        this.code=code;
        this.desc=desc;
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
