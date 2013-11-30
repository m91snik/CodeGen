/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 9/21/13
 * Created by: Nikolay Garbuzov
 */
package com.donriver.example.code_gen.test.generator.proxy;

import com.donriver.example.code_gen.test.generator.test_target.TestTarget;

// this test_target is just for testing purposes
public interface TestAsmProxy {
    //TestResponse doTestRequest(TestRequest testRequest, TestRequest2 testRequest2) throws TestException;

    TestTarget getTarget();

    void setter(boolean val);
    boolean getter();
}
