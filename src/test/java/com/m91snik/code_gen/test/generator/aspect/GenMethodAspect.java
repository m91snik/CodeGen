package com.m91snik.code_gen.test.generator.aspect;

import com.m91snik.code_gen.test.generator.protocol.TestResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created by m91snik on 01.12.13.
 */
@Aspect
@Component
public class GenMethodAspect {

    @Pointcut(
            "execution(@com.m91snik.code_gen.test.generator.annotation.GenMethodAnnotation public com.m91snik.code_gen.test.generator.protocol.TestResponse *(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public TestResponse doAspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        TestResponse result = (TestResponse) proceedingJoinPoint.proceed();
        System.out.println("Aspect is applied for " + proceedingJoinPoint.getTarget());
        return new TestResponse(result.anInt + 1);
    }
}
