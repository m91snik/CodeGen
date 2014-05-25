package com.m91snik.code_gen.test.generator.postconstr.aspect;

import com.m91snik.code_gen.test.generator.postconstr.impl.PcRecalc;
import com.m91snik.code_gen.test.generator.protocol.TestResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

/**
 * Created by m91snik on 23.05.14.
 */
@Aspect
public class PcAspect {

    @Autowired
    private PcRecalc pcRecalc;



    public PcAspect(){
        int a=1;
        System.out.println("PcAspect is created "+this);
        int b=2;
    }

    @PostConstruct
    public void init(){
        System.out.println("PcAspect init "+this);
    }


    @Pointcut(
            "execution(public int com.m91snik.code_gen.test.generator.postconstr.impl.PcInjectable.go())")
    public void pointcut() {
    }

    @Around("pointcut()")
    public int doAspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        int resp = (Integer)proceedingJoinPoint.proceed();
        System.out.println("Aspect"+this+" is applied for " + proceedingJoinPoint.getTarget());
        try {
            return pcRecalc.recalc(resp);
        }catch(NullPointerException e){
            e.printStackTrace();
            return 0;
        }
    }
}
