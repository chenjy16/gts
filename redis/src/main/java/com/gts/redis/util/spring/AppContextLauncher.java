package com.gts.redis.util.spring;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;



public class AppContextLauncher implements ApplicationContextAware{
	
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		AppContextLauncher.applicationContext = appContext;
	}
	
	/**  
     * @return ApplicationContext 
     */  
    public static ApplicationContext getApplicationContext() {  
        return applicationContext;  
    }  
	/**
	 * 获取bean对象
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz){
		Assert.notNull(applicationContext, "Application Context not initialize");
		return applicationContext.getBean(clazz);
	}
	/**
	 * 获取 bean 对象
	 * @param beanName
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(String beanName,Class<T> clazz){
		Assert.notNull(applicationContext, "Application Context not initialize");
		return applicationContext.getBean(beanName, clazz);
	}

}
