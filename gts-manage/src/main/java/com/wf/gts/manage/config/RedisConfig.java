package com.wf.gts.manage.config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@ImportResource("classpath:applicationContext-common.xml")
@ConfigurationProperties(prefix = "config")
public class RedisConfig {

	/**
	 * 功能描述: 配置信息类 <br>
	 * @date: 2017年8月28日 下午4:00:34 
	 * @param maxTotal
	 * @param maxIdle
	 * @param minIdle
	 * @param maxWaitMillis
	 * @param testOnBorrow
	 * @param testOnReturn
	 * @param testWhileIdle
	 * @param timeBetweenEvictionRunsMillis
	 * @param numTestsPerEvictionRun
	 * @param minEvictableIdleTimeMillis
	 * @return
	 */
	@Bean(name = "jedis.config")
	public JedisPoolConfig jedisPoolConfig(@Value("${config.jedis.maxTotal}") int maxTotal,
			@Value("${config.jedis.maxIdle}") int maxIdle, @Value("${config.jedis.minIdle}") int minIdle,
			@Value("${config.jedis.maxWaitMillis}") int maxWaitMillis,
			@Value("${config.jedis.testOnBorrow}") boolean testOnBorrow,
			@Value("${config.jedis.testOnReturn}") boolean testOnReturn,
			@Value("${config.jedis.testWhileIdle}") boolean testWhileIdle,
			@Value("${config.jedis.timeBetweenEvictionRunsMillis}") long timeBetweenEvictionRunsMillis,
			@Value("${config.jedis.numTestsPerEvictionRun}") int numTestsPerEvictionRun,
			@Value("${config.jedis.minEvictableIdleTimeMillis}") long minEvictableIdleTimeMillis) {

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);// 设置最小空闲数
		config.setMaxWaitMillis(maxWaitMillis);
		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);
		// Idle时进行连接扫描
		config.setTestWhileIdle(testWhileIdle);
		// 表示idle object evitor两次扫描之间要sleep的毫秒数
		config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		// 表示idle object evitor每次扫描的最多的对象数
		config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		// 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object
		// evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
		config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

		return config;
	}

	/**
	 * 
	 * 功能描述: 缓存连接类<br>
	 * @date: 2017年8月28日 下午4:00:49 
	 * @param host
	 * @param port
	 * @param config
	 * @return
	 */
	@Bean(name = "jedisConnectionFactory")
	public JedisConnectionFactory jedisConnectionFactory(@Value("${config.jedis.pool.host}") String host,
			@Value("${config.jedis.pool.port}") int port, @Qualifier("jedis.config") JedisPoolConfig config) {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(config);
		jedisConnectionFactory.setHostName(host);
		jedisConnectionFactory.setPort(port);
		return jedisConnectionFactory;
	}
	
	/**
	 * 
	 * 功能描述: 缓存实例<br>
	 * @date: 2017年8月28日 下午4:01:08 
	 * @param connectionFactory
	 * @return
	 */
	@Bean(name = "stringRedisTemplate")
	public StringRedisTemplate stringRedisTemplate(
			@Qualifier("jedisConnectionFactory") JedisConnectionFactory connectionFactory) {
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(connectionFactory);
		return stringRedisTemplate;
	}

}
