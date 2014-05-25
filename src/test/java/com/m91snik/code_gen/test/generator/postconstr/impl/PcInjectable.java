package com.m91snik.code_gen.test.generator.postconstr.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by m91snik on 23.05.14.
 */
@Component
public class PcInjectable {

    @Autowired
    private PcRecalc pcRecalc;

//    public PcInjectable(){
//        System.out.println("PcInjectable is created");
//    }
//
//
    @PostConstruct
    public void init(){
        System.out.println("PcInjectable");
    }

    public int go(){
        return 1;
    }
}
