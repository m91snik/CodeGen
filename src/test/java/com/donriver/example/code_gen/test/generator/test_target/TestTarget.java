/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 9/21/13
 * Created by: Nikolay Garbuzov
 */
package com.donriver.example.code_gen.test.generator.test_target;

import com.donriver.example.code_gen.test.generator.protocol.TestRequest;
import com.donriver.example.code_gen.test.generator.protocol.TestRequest2;
import com.donriver.example.code_gen.test.generator.protocol.TestResponse;

public interface TestTarget {

   TestResponse doTestRequest(TestRequest testRequest, TestRequest2 testRequest2);

}