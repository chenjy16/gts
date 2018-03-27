# gts
全局事务管理


#需要jdk1.8以上
#非强一致性控制，在网络不通等极端情况下可能导致数据不一致，需要告警人工介入
#基于dubbox


一、客户端配置
 
1、注解说明

 //事务发起方超时时间
  long clientTransTimeout() default 3000L;
  
  //事务参与方超时时间
  long serviceTransTimeout() default 3000L;
  
  //请求超时时间
  long socketTimeout() default 3000L;
 
 
2、spring配置

|属性 |描述 |必填|默认值 |备注 |
|---- |----|----|------|----|
|namesrvAddr |注册中心地址 ip：port |是|无 |  |
|instanceName |客户端实例名 |否|DEFAULT |  |
|pollNameServerInterval |从注册服务拉取manage服务地址 |否|1000 * 30 | |
|heartbeatBrokerInterval |像manage服务发送心跳频率 |否|1000 * 30 | |
|timeoutMillis |客户端请求超时时间 |否|3000L |  |
|clientOnewaySemaphoreValue |单向请求流量控制 |否|65535 | |
|clientAsyncSemaphoreValue |异步请求流量控制 |否|65535 | |
|channelNotActiveInterval |检查连接是否关闭 |否|1000 * 60| |
|clientChannelMaxIdleTimeSeconds |客户端连接最大空闲时间 |否|120s | |
|clientSocketSndBufSize |发送缓冲区 |否|65535 | |
|clientSocketRcvBufSize |接收缓冲区 |否|65535 | |
|clientCloseSocketIfTimeout |客户端超时是否关闭连接 |否|false| |



 
3、引入依赖

    <dependency>
    <groupId>com.wf.gts</groupId>
    <artifactId>gts-dubbo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    </dependency>
    
    

二、manage服务部署



三、nameserver服务部署
