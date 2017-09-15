package com.wf.gts.common.entity;

public class TxManagerServer {

    /**
     * TxManagerServer host
     */
    private String host;

    /**
     * TxManagerServer port
     */
    private Integer port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "TxManagerServer{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
