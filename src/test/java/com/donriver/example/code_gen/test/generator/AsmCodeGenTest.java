package com.donriver.example.code_gen.test.generator;

import com.donriver.example.code_gen.test.generator.protocol.TestRequest;
import com.donriver.example.code_gen.test.generator.protocol.TestRequest2;
import com.donriver.example.code_gen.test.generator.proxy.TestAsmProxy;
import com.donriver.example.code_gen.test.generator.test_target.TestTarget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
//        testProxy.setter(true);
//        System.out.println(testProxy.getter());
        TestTarget target = testProxy.getTarget();
        System.out.println(target.doTestRequest(new TestRequest(1), new TestRequest2(2)).anInt);
    }
}
