package com.wufumall.example.b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
@ServletComponentScan
@EnableAspectJAutoProxy(proxyTargetClass=true,exposeProxy=true)
@EnableConfigurationProperties
public class ExampleBSpringBootStrap extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ExampleBSpringBootStrap.class, args);
	}
}
