package com.donriver.example.code_gen.test.generator;

/**
 * Created by m91snik on 03.12.13.
 */

import com.donriver.example.code_gen.test.generator.protocol.TestRequest;
import com.donriver.example.code_gen.test.generator.protocol.TestRequest2;
import com.donriver.example.code_gen.test.generator.protocol.TestResponse;
import com.donriver.example.code_gen.test.generator.test_target.TestTarget;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Show case test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/services/generator/aspects-config.xml"})
public class GenMethodAspectTest {

    @Autowired
    private TestTarget testTarget;

    @Test
    public void test() {
        TestResponse testResponse = testTarget.doTestRequest(new TestRequest(1), new TestRequest2(1));
        Assert.assertEquals(3, testResponse.anInt);
    }
}
