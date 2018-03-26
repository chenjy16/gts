package com.wf.gts.manage.entity;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GtsManagerInfo {

    private String ip;
    private int port;
    private int maxConnection;
    private int nowConnection;
    private int transactionWaitMaxTime;
    private int redisSaveMaxTime;
    private List<String> clusterInfoList;

}
