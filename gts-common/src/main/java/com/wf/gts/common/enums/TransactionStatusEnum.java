package com.wf.gts.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 事务状态枚举定义
 */
public enum TransactionStatusEnum {

    ROLLBACK(0, "回滚"),
    COMMIT(1, "已经提交"),
    BEGIN(2, "开始"),
    RUNNING(3, "执行中"),
    FAILURE(4, "失败"),
    PRE_COMMIT(5, "预提交");


    private int code;

    private String desc;

    TransactionStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static TransactionStatusEnum acquireByCode(int code) {
      
        Optional<TransactionStatusEnum> transactionStatusEnum =
                Arrays.stream(TransactionStatusEnum.values())
                        .filter(v -> Objects.equals(v.getCode(), code))
                        .findFirst();
        
        return transactionStatusEnum.orElse(TransactionStatusEnum.BEGIN);
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
