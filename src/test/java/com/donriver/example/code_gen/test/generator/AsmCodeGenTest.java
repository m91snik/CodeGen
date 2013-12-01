package com.donriver.example.code_gen.test.generator;

import com.donriver.example.code_gen.annotation.GenClassAnnotation;
import com.donriver.example.code_gen.annotation.GenEnum;
import com.donriver.example.code_gen.annotation.GenMethodAnnotation;
import com.donriver.example.code_gen.test.generator.protocol.TestRequest;
import com.donriver.example.code_gen.test.generator.protocol.TestRequest2;
import com.donriver.example.code_gen.test.generator.protocol.TestResponse;
import com.donriver.example.code_gen.test.generator.proxy.TestAsmProxy;
import com.donriver.example.code_gen.test.generator.proxy.TestCgLibProxy;
import com.donriver.example.code_gen.test.generator.test_target.TestTarget;
import com.donriver.example.code_gen.test.generator.utils.TestUtils;
import com.donriver.example.code_gen.util.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;

/**
 * Created by m91snik on 22.11.13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/services/generator/asm-test-root.xml"})
public class AsmCodeGenTest {

    @Autowired
    private TestTarget testTarget;
    @Autowired
    private TestAsmProxy testProxy;


    @Test
    public void test() throws Exception {
        assertMethodWorking();
        assertProxyClassName();
        assertProxyAnnotations();
        assertProxyMethodsAnnotations();
    }

    private void assertMethodWorking() throws TestException {
        TestResponse testResponse = testProxy.doTestRequest(new TestRequest(1), new TestRequest2(2));
        Assert.assertEquals(3, testResponse.anInt);
    }

    private void assertProxyClassName() {
        String expectedClassName = "com.donriver.example.code_gen.test.generator.proxy.TestAsmProxyImpl";
        Assert.assertEquals(expectedClassName, testProxy.getClass().getName());
    }

    private void assertProxyAnnotations() {
        GenClassAnnotation localFacadeBusinessServiceAnnotation =
                testTarget.getClass().getAnnotation(GenClassAnnotation.class);
        GenClassAnnotation facadeBusinessServiceAnnotation =
                testProxy.getClass().getAnnotation(GenClassAnnotation.class);
        Assert.assertEquals(localFacadeBusinessServiceAnnotation, facadeBusinessServiceAnnotation);
    }


    private void assertProxyMethodsAnnotations() {
        Method[] proxyInterfaceMethods = TestAsmProxy.class.getDeclaredMethods();
        Method[] targetInterfaceMethods = TestTarget.class.getDeclaredMethods();

        for (Method method : testProxy.getClass().getDeclaredMethods()) {
            if (!TestUtils.isOverriddenMethod(method, proxyInterfaceMethods)) {
                continue;
            }
            GenMethodAnnotation annotation = method.getAnnotation(GenMethodAnnotation.class);
            Assert.assertNotNull(annotation);
            Method targetMethod = getOverridenMethod(method, targetInterfaceMethods);
            GenMethodAnnotation targetAnnotation = targetMethod.getAnnotation(GenMethodAnnotation.class);
            Assert.assertEquals(targetAnnotation.genEnum(), annotation.genEnum());
        }
    }

    private Method getOverridenMethod(Method method, Method[] targetInterfaceMethods) {
        for (Method targetMethod : testTarget.getClass().getDeclaredMethods()) {
            if (!TestUtils.isOverriddenMethod(targetMethod, targetInterfaceMethods)) {
                continue;
            }
            if (!Utils.essentiallyEqualMethods(method, targetMethod)) {
                continue;
            }
            return targetMethod;
        }
        throw new IllegalStateException("Method " + method + "is not found in target methods " +
                                                targetInterfaceMethods);
    }
}
