# gts
全局事务管理


-需要jdk1.8以上
-非强一致性控制，在网络不通等极端情况下可能导致数据不一致，需要告警人工介入
-基于dubbox


一、客户端配置
 
1、注解说明

 
 
2、spring配置

|属性 |描述 |必填|默认值 |备注 |
|---- |----|----|------|----|
 



 
3、引入依赖

    <dependency>
    <groupId>com.wf.gts</groupId>
    <artifactId>gts-dubbo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    </dependency>
    
    

二、manage服务部署



三、nameserver服务部署
