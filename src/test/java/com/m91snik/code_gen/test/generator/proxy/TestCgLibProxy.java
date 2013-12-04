/**
 * Copyright m91snik
 * Created on: 9/21/13
 * Created by: m91snik
 */
package com.m91snik.code_gen.test.generator.proxy;

import com.m91snik.code_gen.test.generator.TestException;
import com.m91snik.code_gen.test.generator.protocol.TestRequest;
import com.m91snik.code_gen.test.generator.protocol.TestRequest2;
import com.m91snik.code_gen.test.generator.protocol.TestResponse;

// this test_target is just for testing purposes
public interface TestCgLibProxy {
    public abstract TestResponse doTestRequest(TestRequest testRequest, TestRequest2 testRequest2) throws TestException;
}
