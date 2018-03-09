/*package com.wf.gts.manage.config;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.wf.gts.manage.job.CleanCommitTxTransactionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;

@Configuration
public class CleanCommitTxTransactionJobConfig {
  
    @Resource
    private ZookeeperRegistryCenter regCenter;
    
    @Bean
    public CleanCommitTxTransactionJob simpleJob() {
        return new CleanCommitTxTransactionJob(); 
    }

    @Bean(initMethod = "init")
    public JobScheduler simpleJobScheduler(final SimpleJob simpleJob,
                                           @Value("${cleanCommitTxGroupJob.cron}") final String cron,
                                           @Value("${cleanCommitTxGroupJob.shardingTotalCount}") final int shardingTotalCount,
                                           @Value("${cleanCommitTxGroupJob.shardingItemParameters}") final String shardingItemParameters) {
          
    	return new SpringJobScheduler(simpleJob, regCenter, getLiteJobConfiguration(simpleJob.getClass(),
                cron, shardingTotalCount, shardingItemParameters));
    }

    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron, final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration.newBuilder(

            new SimpleJobConfiguration(
                JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount)
                .shardingItemParameters(shardingItemParameters).build(),
                jobClass.getCanonicalName()))
                .overwrite(true).build();//true表示本地配置覆盖zk配置
    }
}*/