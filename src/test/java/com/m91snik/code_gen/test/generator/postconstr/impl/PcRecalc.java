package com.m91snik.code_gen.test.generator.postconstr.impl;

import com.m91snik.code_gen.test.generator.postconstr.aspect.PcAspect;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by m91snik on 23.05.14.
 */
@Component
public class PcRecalc {

//    public PcRecalc(){
//        System.out.println("PcRecalc is created");
//    }
//
    @PostConstruct
    public void init(){
        System.out.println("PcRecalc");
    }

    public int recalc(int a){
        return a+1;
    }

}
