/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 9/21/13
 * Created by: Nikolay Garbuzov
 */
package com.donriver.example.code_gen.test.generator.proxy;

import com.donriver.example.code_gen.test.generator.TestException;
import com.donriver.example.code_gen.test.generator.protocol.TestRequest;
import com.donriver.example.code_gen.test.generator.protocol.TestRequest2;
import com.donriver.example.code_gen.test.generator.protocol.TestResponse;

// this test_target is just for testing purposes
public interface TestCgLibProxy {
    public abstract TestResponse doTestRequest(TestRequest testRequest, TestRequest2 testRequest2) throws TestException;
}
