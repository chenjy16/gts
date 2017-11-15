package com.wufumall.example.c.config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;
import java.util.ArrayList;
import java.util.List;


@Configuration
@ImportResource("classpath:applicationContext-common.xml")
@ConfigurationProperties(prefix = "config")
public class RedisConfig {

  @Bean(name = "shardedJedisPool")
  public ShardedJedisPool shardedJedisPool(@Qualifier("jedis.config") JedisPoolConfig config,
                                           @Qualifier("jedisShardInfo1") JedisShardInfo jedisShardInfo1,
                                           @Qualifier("jedisShardInfo2") JedisShardInfo jedisShardInfo2) {
    List<JedisShardInfo> list = new ArrayList<>();
    list.add(jedisShardInfo1);
    list.add(jedisShardInfo2);
    return new ShardedJedisPool(config, list);
  }


  @Bean(name = "jedisShardInfo1")
  public JedisShardInfo jedisShardInfo1(@Value("${config.jedis.jedisShardInfo1.host}") String host,
                                       @Value("${config.jedis.jedisShardInfo1.port}") int port) {
    JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port);
    return jedisShardInfo;
  }

  
  @Bean(name = "jedisShardInfo2")
  public JedisShardInfo jedisShardInfo2(@Value("${config.jedis.jedisShardInfo2.host}") String host,
                                       @Value("${config.jedis.jedisShardInfo2.port}") int port) {
    JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port);
    return jedisShardInfo;
  }

  
  @Bean(name = "jedis.config")
  public JedisPoolConfig jedisPoolConfig(@Value("${config.jedis.maxTotal}") int maxTotal,
                                         @Value("${config.jedis.maxIdle}") int maxIdle,
                                         @Value("${config.jedis.maxWaitMillis}") int maxWaitMillis,
                                         @Value("${config.jedis.testOnBorrow}") boolean testOnBorrow,
                                         @Value("${config.jedis.testOnReturn}") boolean testOnReturn) {

    JedisPoolConfig config = new JedisPoolConfig();
    config.setMaxTotal(maxTotal);
    config.setMaxIdle(maxIdle);
    config.setMaxWaitMillis(maxWaitMillis);
    return config;
  }



}
