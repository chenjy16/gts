package com.wf.gts.manage.entity;
import java.util.List;

public class TxManagerInfo {

    private String ip;
    private int port;
    private int maxConnection;
    private int nowConnection;
    private int transactionWaitMaxTime;
    private int redisSaveMaxTime;
    private List<String> clusterInfoList;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public int getNowConnection() {
        return nowConnection;
    }

    public void setNowConnection(int nowConnection) {
        this.nowConnection = nowConnection;
    }

    public int getTransactionWaitMaxTime() {
        return transactionWaitMaxTime;
    }

    public void setTransactionWaitMaxTime(int transactionWaitMaxTime) {
        this.transactionWaitMaxTime = transactionWaitMaxTime;
    }

    public int getRedisSaveMaxTime() {
        return redisSaveMaxTime;
    }

    public void setRedisSaveMaxTime(int redisSaveMaxTime) {
        this.redisSaveMaxTime = redisSaveMaxTime;
    }

    public List<String> getClusterInfoList() {
        return clusterInfoList;
    }

    public void setClusterInfoList(List<String> clusterInfoList) {
        this.clusterInfoList = clusterInfoList;
    }

    @Override
    public String toString() {
        return "TxManagerInfo{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", maxConnection=" + maxConnection +
                ", nowConnection=" + nowConnection +
                ", transactionWaitMaxTime=" + transactionWaitMaxTime +
                ", redisSaveMaxTime=" + redisSaveMaxTime +
                ", clusterInfoList=" + clusterInfoList +
                '}';
    }
}
