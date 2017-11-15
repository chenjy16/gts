package com.wufumall.example.c.config;

import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;
import java.util.Properties;

/**
 * @ClassName: DataSourceConfig
 * @Description: 数据源配
 * @author
 * @date 20170531 下午8:07:44
 * 
 */
@ComponentScan(value = "com.wufumall.example")
@Configuration
@EnableTransactionManagement
@MapperScan("com.wufumall.example.*.dao")
//@MapperScan("com.wufumall.user.dao")
//@MapperScan("com.wufumall.*.dao")
//@MapperScan(basePackages = {"com.wufumall.user.dao", "com.wufumall.message.dao"})
public class DataSourceConfig {

	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;
	@Value("${spring.datasource.driverClassName}")
	private String driverClassName;
	@Value("${spring.datasource.maxActive}")
    private Integer maxActive;
    @Value("${spring.datasource.minIdle}")
    private Integer minIdle;
    @Value("${spring.datasource.maxWait}")
    private Integer maxWait;
    @Value("${spring.datasource.initialSize}")
    private Integer initialSize;
    
    /** 是否自动回收超时连接 */
    @Value("${spring.datasource.removeAbandoned}")
    private Boolean removeAbandoned;
    /**  超时时间(以秒数为单位) */
    @Value("${spring.datasource.removeAbandonedTimeout}")
    private Integer removeAbandonedTimeout;
    /** 打开检查,用异步线程evict进行检查 */
    @Value("${spring.datasource.testWhileIdle}")
    private Boolean testWhileIdle;
    /** 获取连接前是否运行validationQuery,true=运行[默认],false=不运行 */
    @Value("${spring.datasource.testOnBorrow}")
    private Boolean testOnBorrow;
    /** 将连接归还连接池前是否运行validationQuery,true=运行,false=不运行[默认] */
    @Value("${spring.datasource.testOnReturn}")
    private Boolean testOnReturn;
    /** 检查连接,应用的SQL语句执行之前运行一次 */
    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;
    /** 回收资源的数量  */
    @Value("${spring.datasource.numTestsPerEvictionRun}")
    private Integer numTestsPerEvictionRun;
    /** 资源最小空闲时间   */
    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private Integer minEvictableIdleTimeMillis;

	@Bean(destroyMethod = "close")
	@Primary
	public DataSource dataSource() {
		DataSource datasource = new DataSource();
		datasource.setUrl(url);
		datasource.setUsername(username);
		datasource.setPassword(password);
		datasource.setDriverClassName(driverClassName);		
		/** --配置mysql数据库的连接池
        maxIdle 连接池中最多可空闲maxIdle个连接
        minIdle 连接池中最少空闲maxIdle个连接
        initialSize 初始化连接数目
        maxWait 连接池中连接用完时,新的请求等待时间,毫秒
        username 数据库用户名
        password 数据库密码
        */
		datasource.setMaxActive(maxActive);
        datasource.setMinIdle(minIdle);
        datasource.setMaxWait(maxWait);
        datasource.setInitialSize(initialSize);
        datasource.setRemoveAbandoned(removeAbandoned);
        datasource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setValidationQuery("select 1");
        datasource.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        
		return datasource;
	}

	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setFailFast(true);

		sessionFactory.setTypeAliasesPackage("com.wufumall.example.c.model");
		
		// 分页插件,插件无非是设置mybatis的拦截器
		PageHelper pageHelper = new PageHelper();
		Properties properties = new Properties();
		properties.setProperty("reasonable", "true");
		properties.setProperty("supportMethodsArguments", "true");
		properties.setProperty("returnPageInfo", "check");
		properties.setProperty("params", "count=countSql");
		pageHelper.setProperties(properties);
		sessionFactory.setPlugins(new Interceptor[] { pageHelper });
		// sessionFactory.setMapperLocations(new
		// PathMatchingResourcePatternResolver().getResources("classpath*:com/wufumall/**/*Mapper.xml"));
		sessionFactory
				.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapping/*.xml"));
		sessionFactory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
		return sessionFactory.getObject();
	}
}
