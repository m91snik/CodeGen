package com.donriver.example.code_gen.test.generator.utils;

import com.donriver.example.code_gen.util.Utils;

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


    public static Method getOverridenMethodFromClazz(Method method, Class clazz, Method[] targetInterfaceMethods) {
        for (Method targetMethod : clazz.getDeclaredMethods()) {
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
