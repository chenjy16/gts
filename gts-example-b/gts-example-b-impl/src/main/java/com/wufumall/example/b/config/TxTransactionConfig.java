package com.wufumall.example.b.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.wf.gts.core.bean.bootstrap.TxTransactionBootstrap;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true,exposeProxy=true)
@ComponentScan(value="com.wufumall.example.*,com.wf.gts.*")
public class TxTransactionConfig {
	
	@Bean(name="txTransactionBootstrap")
	public TxTransactionBootstrap txTransactionBootstrap(@Value("${tx.txManagerUrl}") String txManagerUrl, @Value("${tx.serializer}") String serializer,
			@Value("${tx.nettySerializer}") String nettySerializer,
			@Value("${tx.blockingQueueType}") String blockingQueueType) {
	  
	  
	  TxTransactionBootstrap boot=new TxTransactionBootstrap();
	  boot.setTxManagerUrl(txManagerUrl);
	  boot.setSerializer(serializer);
	  boot.setNettySerializer(nettySerializer);
	  boot.setBlockingQueueType(blockingQueueType);
		return boot;
	}

}
