# gts
全局事务管理





非强一致性控制，在网络不通等极端情况下可能导致数据不一致，需要告警人工介入
 
 
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
 </bean>

 
3、引入依赖
 <dependency>
 <groupId>com.wf.gts</groupId>
 <artifactId>gts-dubbo</artifactId>
 <version>0.0.1-SNAPSHOT</version>
 </dependency>

