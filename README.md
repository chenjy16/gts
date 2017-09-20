一、消息表（任务表） ：适用于java 、 php
1、 将业务操作抽象成一个任务入库，如果业务涉及到的接口成功，更新任务表成功，不成功设置失败状态，通过定时调度做处理，处理方式有三种：
    1)  重试+告警（人工干预）
    2)  撤销+告警（人工干预）
    3)  直接告警(人工干预)
 
2、以订单积分操作为例：
 
 
 
1)用户服务积分增减接口保证幂等性，可以支持重试;
2)积分消息发送mq，可以用定时任务异步发送，并重试3次;
3)对于积分消息表没有确认处理的消息，并且重试3次仍然没有成功的消息，告警出来;
 
二、全局分布式事务服务管理服务gts：非强一致性控制，在网络不通等极端情况下可能导致数据不一致，需要告警人工介入，目前仅支持dubbo
 
1、注解说明
@TxTransaction  该注解为分布式事务的切面（AOP point），如果业务方的service服务需要参与分布式事务，则需要加上此注解
//调用方事务超时时间
int clientTransTimeout() default 3000;

//被调用方事务超时时间
int serviceTransTimeout() default 3000;

//被调用方事务超时时间
int socketTimeout() default 1000;
 
 
2、spring配置
 
   <!-- Aspect 切面配置，是否开启AOP切面-->
   <aop:aspectj-autoproxy expose-proxy="true"/>
   <!--扫描分布式事务的包-->
   <context:component-scan base-package="com.xxx.xxx.*"/>
   <!--启动类属性配置-->
   <bean id="txTransactionBootstrap" class="com.wf.gts.core.bean.bootstrap.TxTransactionBootstrap">
       <property name="txManagerUrl" value="http://192.168.1.66:8761"/>
       <property name="serializer" value="kryo"/>
       <property name="nettySerializer" value="kryo"/>
       <property name="blockingQueueType" value="Linked"/>
       <property name="compensation" value="true"/>
       <property name="compensationCacheType" value="db"/>
       <property name="txDbConfig">
           <bean class="com.wf.gts.core.config.TxDbConfig">
               <property name="url"
                         value="jdbc:mysql://192.168.1.78:3306/order?useUnicode=true&amp;characterEncoding=utf8"/>
               <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
               <property name="password" value="password"/>
               <property name="username" value="xiaoyu"/>
           </bean>
       </property>
   </bean>

 
3、引入依赖
<dependency>
<groupId>com.wf.gts</groupId>
<artifactId>gts-dubbo</artifactId>
<version>0.0.1-SNAPSHOT</version>
</dependency>

