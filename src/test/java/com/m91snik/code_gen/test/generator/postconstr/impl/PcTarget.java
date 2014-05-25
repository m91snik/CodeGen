package com.m91snik.code_gen.test.generator.postconstr.impl;

import com.m91snik.code_gen.test.generator.postconstr.aspect.PcAspect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by m91snik on 23.05.14.
 */
@Component
@Lazy
public class PcTarget implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private PcInjectable pcInjectable;

    @PostConstruct
    public void init(){
        System.out.println("PcTarget");
        System.out.println(pcInjectable.go());
    }

    public int getA(){
        return pcInjectable.go();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
