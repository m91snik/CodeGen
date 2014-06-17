/**
 * Copyright m91snik
 * Created on: 9/21/13
 * Created by: m91snik
 */
package com.m91snik.code_gen.test.generator;

import com.m91snik.code_gen.test.generator.annotation.GenClassAnnotation;
import com.m91snik.code_gen.test.generator.annotation.GenEnum;
import com.m91snik.code_gen.test.generator.annotation.GenMethodAnnotation;
import com.m91snik.code_gen.test.generator.protocol.TestRequest;
import com.m91snik.code_gen.test.generator.protocol.TestRequest2;
import com.m91snik.code_gen.test.generator.protocol.TestResponse;
import com.m91snik.code_gen.test.generator.proxy.TestCgLibProxy;
import com.m91snik.code_gen.test.generator.test_target.TestTarget;
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
        assertMethodWorkingWithoutAspects();
        assertProxyClassName();
    }

    private void assertProxyClassName() {
        String expectedClassName = "com.m91snik.code_gen.test.generator.proxy.TestCgLibProxyImpl";
        Assert.assertEquals(expectedClassName, testProxy.getClass().getName());
    }

    private void assertMethodWorkingWithoutAspects() throws TestException {
        TestRequest testRequest = new TestRequest(1);
        TestRequest2 testRequest2 = new TestRequest2(2);
        TestResponse testResponse = testProxy.doTestRequest(testRequest, testRequest2);

        Assert.assertEquals(4, testResponse.anInt);
    }

}
