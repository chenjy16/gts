
package com.wf.gts.common.enums;


import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 线程池阻塞队列枚举
 */
public enum BlockingQueueTypeEnum {

    LINKED_BLOCKING_QUEUE("Linked"),
    ARRAY_BLOCKING_QUEUE("Array"),
    SYNCHRONOUS_QUEUE("SynchronousQueue");

    private String value;

    BlockingQueueTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BlockingQueueTypeEnum fromString(String value) {
        Optional<BlockingQueueTypeEnum> blockingQueueTypeEnum =
                Arrays.stream(BlockingQueueTypeEnum.values())
                        .filter(v -> Objects.equals(v.getValue(), value))
                        .findFirst();
        return blockingQueueTypeEnum.orElse(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE);
    }

    public String toString() {
        return value;
    }
}

