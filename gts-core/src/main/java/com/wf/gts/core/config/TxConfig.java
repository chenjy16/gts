package com.wf.gts.core.config;

/**
 * TxTransaction 事务基本信息配置类
 */
public class TxConfig {
    /**
     * 提供不同的序列化对象 
     */
    private String serializer = "kryo";
    /**
     * netty 传输的序列化协议
     */
    private String nettySerializer = "kryo";
    /**
     * 延迟时间
     */
    private int delayTime = 30;

    /**
     * 执行事务的线程数大小
     */
    private int transactionThreadMax = Runtime.getRuntime().availableProcessors() << 1;
    /**
     * netty 工作线程大小
     */
    private int nettyThreadMax = Runtime.getRuntime().availableProcessors() << 1;
    /**
     * 心跳时间 默认10秒
     */
    private int  heartTime =10;

    /**
     * 线程池的拒绝策略
     */
    private String rejectPolicy = "Abort";

    /**
     * 线程池的队列类型 
     */
    private String blockingQueueType = "Linked";




    /**
     * 更新tmInfo 的时间间隔
     */
    private int refreshInterval=60;


    /**
     * txManagerUrl服务地址
     */
    private String txManagerUrl;



    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }


    public String getTxManagerUrl() {
        return txManagerUrl;
    }

    public void setTxManagerUrl(String txManagerUrl) {
        this.txManagerUrl = txManagerUrl;
    }



    public String getRejectPolicy() {
        return rejectPolicy;
    }

    public void setRejectPolicy(String rejectPolicy) {
        this.rejectPolicy = rejectPolicy;
    }

    public String getBlockingQueueType() {
        return blockingQueueType;
    }

    public void setBlockingQueueType(String blockingQueueType) {
        this.blockingQueueType = blockingQueueType;
    }

    public int getTransactionThreadMax() {
        return transactionThreadMax;
    }

    public void setTransactionThreadMax(int transactionThreadMax) {
        this.transactionThreadMax = transactionThreadMax;
    }

    public String getNettySerializer() {
        return nettySerializer;
    }

    public void setNettySerializer(String nettySerializer) {
        this.nettySerializer = nettySerializer;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int getNettyThreadMax() {
        return nettyThreadMax;
    }

    public void setNettyThreadMax(int nettyThreadMax) {
        this.nettyThreadMax = nettyThreadMax;
    }

    public int getHeartTime() {
        return heartTime;
    }

    public void setHeartTime(int heartTime) {
        this.heartTime = heartTime;
    }


    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
}
