package com.wf.gts.core.bean.bootstrap;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;


/**
 * 自定义扫描类
 */
public class Scanner extends ClassPathBeanDefinitionScanner {
    public Scanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }

}
