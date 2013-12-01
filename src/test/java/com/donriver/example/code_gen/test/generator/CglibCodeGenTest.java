/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 9/21/13
 * Created by: Nikolay Garbuzov
 */
package com.donriver.example.code_gen.test.generator;

import com.donriver.example.code_gen.test.generator.annotation.GenClassAnnotation;
import com.donriver.example.code_gen.test.generator.annotation.GenEnum;
import com.donriver.example.code_gen.test.generator.annotation.GenMethodAnnotation;
import com.donriver.example.code_gen.test.generator.protocol.TestRequest;
import com.donriver.example.code_gen.test.generator.protocol.TestRequest2;
import com.donriver.example.code_gen.test.generator.protocol.TestResponse;
import com.donriver.example.code_gen.test.generator.proxy.TestCgLibProxy;
import com.donriver.example.code_gen.test.generator.test_target.TestTarget;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/services/generator/cglib-test-root.xml"})
public class CglibCodeGenTest {

    @Autowired
    private TestTarget testTarget;
    @Autowired
    private TestCgLibProxy testProxy;

    @Test
    public void testWebServiceFacadeGenerator() throws Exception {
        assertMethodWorking();
        assertProxyClassName();
        //assertProxyAnnotations();
        //assertProxyMethodsAnnotations();
    }

    private void assertProxyClassName() {
        String expectedClassName = "com.donriver.example.code_gen.test.generator.proxy.TestCgLibProxyImpl";
        Assert.assertEquals(expectedClassName, testProxy.getClass().getName());
    }

    private void assertMethodWorking() throws TestException {
        TestRequest testRequest = new TestRequest(1);
        TestRequest2 testRequest2 = new TestRequest2(2);
        TestResponse expectedTestResponse = testTarget.doTestRequest(testRequest, testRequest2);
        TestResponse testResponse = testProxy.doTestRequest(testRequest, testRequest2);

        Assert.assertEquals(expectedTestResponse, testResponse);
    }

    private void assertProxyAnnotations() {
        GenClassAnnotation localFacadeBusinessServiceAnnotation =
                testTarget.getClass().getAnnotation(GenClassAnnotation.class);
        GenClassAnnotation facadeBusinessServiceAnnotation =
                testProxy.getClass().getAnnotation(GenClassAnnotation.class);
        Assert.assertEquals(localFacadeBusinessServiceAnnotation, facadeBusinessServiceAnnotation);
    }

    private void assertProxyMethodsAnnotations() {
        Method[] interfaceMethods = TestCgLibProxy.class.getDeclaredMethods();
        for (Method method : testProxy.getClass().getDeclaredMethods()) {
            if (!isOverriddenMethod(method, interfaceMethods)) {
                continue;
            }
            GenMethodAnnotation annotation = method.getAnnotation(GenMethodAnnotation.class);
            Assert.assertNotNull(annotation);
            Assert.assertEquals(GenEnum.SECOND, annotation.genEnum());
        }
    }

    private boolean isOverriddenMethod(Method method, Method[] interfaceMethods) {
        String methodName = method.getName();
        for (Method interfaceMethod : interfaceMethods) {
            if (methodName.equals(interfaceMethod.getName())) {
                return true;
            }
        }
        return false;
    }
}
