package com.m91snik.code_gen.test.generator.test_target.impl;

import com.m91snik.code_gen.test.generator.annotation.GenClassAnnotation;
import com.m91snik.code_gen.test.generator.annotation.GenMethodAnnotation;
import com.m91snik.code_gen.test.generator.component.Calculator;
import com.m91snik.code_gen.test.generator.protocol.TestRequest;
import com.m91snik.code_gen.test.generator.protocol.TestRequest2;
import com.m91snik.code_gen.test.generator.protocol.TestResponse;
import com.m91snik.code_gen.test.generator.test_target.TestTarget;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;

@Named("testTarget")
@GenClassAnnotation(serviceName = "TestService", loggingChannels = {"channel1"})
public class TestTargetImpl implements TestTarget {

    @Autowired
    private Calculator calculator;

    @Override
    @GenMethodAnnotation
    public TestResponse doTestRequest(TestRequest testRequest, TestRequest2 testRequest2) {
        TestResponse testResponse = new TestResponse();
        testResponse.anInt = calculator.sum(testRequest.anInt, testRequest2.anInt2);
        return testResponse;
    }


}
