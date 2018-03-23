package com.wf.gts.manage.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Component
@ConfigurationProperties(ignoreUnknownFields = false,prefix = "gts.manage")
public class GtsManageConfig {
  
  private String namesrvAddr;
  private String manageName;
  private long manageId;
  private int registerBrokerTimeoutMills = 6000;

  private int defaultThreadPoolNums;
  private int clientManageThreadPoolNums;
  private int clientManagerThreadPoolQueueCapacity;
  
  
  
}
