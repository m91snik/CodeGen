package com.donriver.example.code_gen.test.generator.utils;

import java.lang.reflect.Method;

/**
 * Created by m91snik on 01.12.13.
 */
public class TestUtils {


    public static boolean isOverriddenMethod(Method method, Method[] interfaceMethods) {
        String methodName = method.getName();
        for (Method interfaceMethod : interfaceMethods) {
            if (methodName.equals(interfaceMethod.getName())) {
                return true;
            }
        }
        return false;
    }
}
