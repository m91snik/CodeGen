/**
 * Copyright m91snik
 * Created on: 9/21/13
 * Created by: m91snik
 */
package com.m91snik.code_gen.registrator;

import com.m91snik.code_gen.generator.impl.AsmClassGenerator;
import com.m91snik.code_gen.util.Utils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

public class AsmClassRegistrator implements ApplicationListener<ContextRefreshedEvent>,
        ApplicationContextAware {

    @Autowired
    private AsmClassGenerator classGenerator;

    private Map<Object, String> targetImplToProxyMap;
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        generateAndRegisterProxyImpl();
    }

    private void generateAndRegisterProxyImpl() {
        try {
            for (Map.Entry<Object, String> entry : targetImplToProxyMap.entrySet()) {
                Class proxyClass = Utils.getClassBySimpleName(entry.getValue());
                Class proxyClassImpl =
                        classGenerator.generate(entry.getKey().getClass(), proxyClass);

                String proxyClassImplName = extractProxyClassImplName(proxyClass.getName());
                BeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(proxyClassImpl);
                BeanDefinitionRegistry beanDefinitionRegistry =
                        (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
                beanDefinitionRegistry.registerBeanDefinition(proxyClassImplName, beanDefinition);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot generate web facades", e);
        }
    }

    private String extractProxyClassImplName(String webFacadeNameClassName) {
        int indexOfLastDot = webFacadeNameClassName.lastIndexOf(".");
        String webFacadeName = webFacadeNameClassName.substring(indexOfLastDot + 1);
        String webServiceFacadeImplBeanName = webFacadeName.substring(0, 1).toLowerCase() + webFacadeName.substring(1);
        return webServiceFacadeImplBeanName;
    }

    public void setTargetImplToProxyMap(Map<Object, String> targetImplToProxyMap) {
        this.targetImplToProxyMap = targetImplToProxyMap;
    }

    public void setClassGenerator(AsmClassGenerator classGenerator) {
        this.classGenerator = classGenerator;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
