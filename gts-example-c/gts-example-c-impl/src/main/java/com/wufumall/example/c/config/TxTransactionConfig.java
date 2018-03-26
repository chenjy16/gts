package com.wufumall.example.c.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.wf.gts.core.bootstrap.TxTransactionBootstrap;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true,exposeProxy=true)
@ComponentScan(value="com.wufumall.example.*,com.wf.gts.*")
public class TxTransactionConfig {
	
	@Bean(name="txTransactionBootstrap")
	public TxTransactionBootstrap txTransactionBootstrap(@Value("${tx.namesrvAddr}") String namesrvAddr) {
	  TxTransactionBootstrap boot=new TxTransactionBootstrap();
	  boot.setNamesrvAddr(namesrvAddr);
		return boot;
	}

}
