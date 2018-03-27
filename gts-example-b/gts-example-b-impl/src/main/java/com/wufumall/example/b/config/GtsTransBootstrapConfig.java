package com.wufumall.example.b.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import com.wf.gts.core.bootstrap.GtsTransBootstrap;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true,exposeProxy=true)
@ComponentScan(value="com.wufumall.example.*,com.wf.gts.*")
public class GtsTransBootstrapConfig {
	
  @Bean(name="gtsTransBootstrap")
  public GtsTransBootstrap gtsTransBootstrap(@Value("${gts.namesrvAddr}") String namesrvAddr) {
    GtsTransBootstrap boot=new GtsTransBootstrap();
    boot.setNamesrvAddr(namesrvAddr);
    return boot;
  }

}
