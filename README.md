# gts
全局事务管理


#需要jdk1.8以上
#非强一致性控制，在网络不通等极端情况下可能导致数据不一致，需要告警人工介入
#基于dubbox



![Image text](https://github.com/chenjy16/gts/blob/master/gts.png)



一、客户端配置
 
1、注解说明

 GtsTransaction  该注解为分布式事务的切面（AOP point），如果业务方的service服务需要参与分布式事务，则需要加上此注解

 //事务发起方超时时间
  long clientTransTimeout() default 3000L;
  
  //事务参与方超时时间
  long serviceTransTimeout() default 3000L;
  
  //请求超时时间
  long socketTimeout() default 3000L;
 
 
2、spring配置

|属性 |描述 |必填|默认值 |备注 |
|---- |----|----|------|----|
|namesrvAddr |注册中心地址 ip：port |是|无 | 暂时不支持多个 |
|instanceName |客户端实例名 |否|DEFAULT |  |
|pollNameServerInterval |从注册服务拉取manage服务地址 |否|1000 * 30 ms | |
|heartbeatBrokerInterval |向manage服务发送心跳频率 |否|1000 * 30 ms| |
|timeoutMillis |客户端请求超时时间 |否|3000 ms |  |
|clientOnewaySemaphoreValue |单向请求流量控制 |否|65535 | 请求数量|
|clientAsyncSemaphoreValue |异步请求流量控制 |否|65535 |请求数量 |
|channelNotActiveInterval |检查连接是否关闭 |否|1000 * 60 ms| |
|clientChannelMaxIdleTimeSeconds |客户端连接最大空闲时间 |否|120s | |
|clientSocketSndBufSize |发送缓冲区 |否|65535 |64k |
|clientSocketRcvBufSize |接收缓冲区 |否|65535 |64k |
|clientCloseSocketIfTimeout |客户端超时是否关闭连接 |否|false| |



 
3、引入依赖

    <dependency>
    <groupId>com.wf.gts</groupId>
    <artifactId>gts-dubbo</artifactId>
    <version>0.0.4-SNAPSHOT</version>
    </dependency>
    
    

二、manage服务部署


|属性 |描述 |必填|默认值 |备注 |
|---- |----|----|------|----|
|namesrvAddr |注册中心地址 ip：port;ip：port |是|无 | 多个用逗号分隔 |
|manageName |manage实例名 |否| |  |
|manageId |manage标识|是|1 |支持主备模式ha,  配置为 1：主 , 否则为备 |
|registerBrokerTimeoutMills |注册manage地址向nameserver的请求超时时间 |否|6000 ms | |
|defaultThreadPoolNums |默认线程池数量|是| |  |
|clientManageThreadPoolNums |客户端连接管理的线程池数量 |是| | |
|clientManagerThreadPoolQueueCapacity |客户端连接管理的线程池队列数量 |是| | |




 netty.server:

|属性 |描述 |必填|默认值 |备注 |
|---- |----|----|------|----|
|listenPort |注册中心端口  |是|无 |  |
|serverWorkerThreads |工作线程数量 |否|4 |  |
|serverCallbackExecutorThreads |执行回调方法的线程数量 |否|4 | |
|serverSelectorThreads |socket io线程数 |否|3 | |
|serverOnewaySemaphoreValue |单向请求流量控制|否|256| 请求数量 |
|serverAsyncSemaphoreValue |异步请求流量控制 |否|64 | 请求数量|
|serverChannelMaxIdleTimeSeconds | 连接最大空闲时间|否|120s | |
|serverSocketSndBufSize |发送缓存区 |否|65535|64k |
|serverSocketRcvBufSize |接收缓存区 |否|65535| 64k|
|serverPooledByteBufAllocatorEnable | BUFFER分配方式|否|true | |
|useEpollNativeSelector |io方式是否使用epoll模式 |否|false | |

netty.client

|属性 |描述 |必填|默认值 |备注 |
|---- |----|----|------|----|
|clientWorkerThreads |工作线程数量 |否|  4| |
|clientCallbackExecutorThreads |执行回调方法的线程数量 |否|cpu数量 | |
|clientOnewaySemaphoreValue |单向请求流量控制 |否|65535 |请求数量 |
|clientAsyncSemaphoreValue |异步请求流量控制 |否|65535 | 请求数量|
|channelNotActiveInterval |检查连接是否关闭 |否|1000 * 60 ms| |
|clientChannelMaxIdleTimeSeconds |客户端连接最大空闲时间 |否|120s | |
|clientSocketSndBufSize |发送缓冲区 |否|65535 | 64k|
|clientSocketRcvBufSize |接收缓冲区 |否|65535 | 64k|
|connectTimeoutMillis |客户端超时是否关闭连接 |否|false| |

三、nameserver服务部署

1、参数配置

|属性 |描述 |必填|默认值 |备注 |
|---- |----|----|------|----|
|listenPort |注册中心端口  |是|无 |  |
|serverWorkerThreads |工作线程数量 |否|4 |  |
|serverCallbackExecutorThreads |执行回调方法的线程数量 |否|4 | |
|serverSelectorThreads |socket io线程数 |否|3 | |
|serverOnewaySemaphoreValue |单向请求流量控制|否|256| 请求数量 |
|serverAsyncSemaphoreValue |异步请求流量控制 |否|64 |请求数量 |
|serverChannelMaxIdleTimeSeconds | 连接最大空闲时间|否| 120 s| |
|serverSocketSndBufSize |发送缓存区 |否|65535| 64k|
|serverSocketRcvBufSize |接收缓存区 |否|65535| 64k|
|serverPooledByteBufAllocatorEnable | BUFFER分配方式|否|true | |
|useEpollNativeSelector |io方式 |否|false | |

