package com.wf.gts.core.util;
import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringBeanUtils {

  private ConfigurableApplicationContext cfgContext;
  private static final SpringBeanUtils INSTANCE = new SpringBeanUtils();
  private SpringBeanUtils() {}

  public static SpringBeanUtils getInstance() {
      return INSTANCE;
  }

  /**
   * 功能描述: 防止序列化产生对象
   * @author: chenjy
   * @date: 2017年9月18日 下午2:27:08 
   * @return
   */
  private Object readResolve() {
      return INSTANCE;
  }

  public <T> T getBean(Class<T> type) {
      return cfgContext.getBean(type);
  }

  public String getBeanName(Class type) {
      return cfgContext.getBeanNamesForType(type)[0];
  }

  /**
   * 功能描述: 判断一个bean是否存在Spring容器中
   * @author: chenjy
   * @date: 2017年9月18日 下午2:27:42 
   * @param type
   * @return
   */
  public boolean exitsBean(Class type) {
      return cfgContext.containsBean(type.getName());
  }

  
  /**
   * 功能描述: 动态注册一个Bean动Spring容器中
   * @author: chenjy
   * @date: 2017年9月18日 下午2:27:58 
   * @param beanName
   * @param beanClazz
   * @param propertys
   */
  public void registerBean(String beanName, Class beanClazz, Map<String, Object> propertys) {
      BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
      if (propertys != null) {
          propertys.forEach((k, v) -> builder.addPropertyValue(k, v));
      }
      builder.setScope(BeanDefinition.SCOPE_SINGLETON);
      registerBean(beanName, builder.getBeanDefinition());

  }

  public void registerBean(String beanName, Object obj) {
      cfgContext.getBeanFactory().registerSingleton(beanName, obj);
  }

  /**
   * 功能描述: 注册Bean信息
   * @author: chenjy
   * @date: 2017年9月18日 下午2:28:15 
   * @param beanName
   * @param beanDefinition
   */
  public void registerBean(String beanName, BeanDefinition beanDefinition) {
      BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) cfgContext.getBeanFactory();
      beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
  }

  /**
   * 功能描述: 根据枚举类型获取Spring注册的Bean
   * @author: chenjy
   * @date: 2017年9月18日 下午2:28:28 
   * @param annotationType
   * @return
   */
  public Map<String, Object> getBeanWithAnnotation(Class<? extends Annotation> annotationType) {
      return cfgContext.getBeansWithAnnotation(annotationType);
  }

  /**
   * 功能描述: 动态注册一个Bean动Spring容器中
   * @author: chenjy
   * @date: 2017年9月18日 下午2:28:42 
   * @param beanName
   * @param beanClazz
   */
  public void registerBean(String beanName, Class beanClazz) {
      registerBean(beanName, beanClazz, null);
  }
  
  public void setCfgContext(ConfigurableApplicationContext cfgContext) {
      this.cfgContext = cfgContext;
  }

}
