package com.wf.gts.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 事务状态枚举定义
 */
public enum TransStatusEnum {

    ROLLBACK(0, "回滚"),
    COMMIT(1, "已经提交"),
    BEGIN(2, "开始"),
    PRE_COMMIT(5, "预提交");

    private int code;

    private String desc;

    TransStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TransStatusEnum acquireByCode(int code) {
        Optional<TransStatusEnum> transactionStatusEnum =
                Arrays.stream(TransStatusEnum.values())
                        .filter(v -> Objects.equals(v.getCode(), code))
                        .findFirst();
        return transactionStatusEnum.orElse(TransStatusEnum.BEGIN);
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
