package com.wf.gts.manage.config;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.wf.gts.manage.domain.NettyParam;


@Configuration
@EnableAutoConfiguration
public class NettyConfig {
  
  @Bean
  @ConfigurationProperties("tx.manager.netty")
  public NettyParam getNettyConfig() {
      return new NettyParam();
  }

}
