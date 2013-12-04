/**
 * Copyright m91snik
 * Created on: 9/21/13
 * Created by: m91snik
 */
package com.m91snik.code_gen.test.generator.test_target;

import com.m91snik.code_gen.test.generator.protocol.TestRequest;
import com.m91snik.code_gen.test.generator.protocol.TestRequest2;
import com.m91snik.code_gen.test.generator.protocol.TestResponse;

public interface TestTarget {

   TestResponse doTestRequest(TestRequest testRequest, TestRequest2 testRequest2);

}