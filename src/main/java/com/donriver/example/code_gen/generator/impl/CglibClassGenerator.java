/**
 * Created by m91snik on 02.10.13.
 */
package com.donriver.example.code_gen.generator.impl;

import com.donriver.example.code_gen.util.Utils;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibClassGenerator {

    public Object generate(final Object targetClass, final Class proxyInterfaceClass) throws Exception {
        Method[] proxyDeclaredMethods = proxyInterfaceClass.getDeclaredMethods();
        final Method[] targetDeclaredMethods = targetClass.getClass().getDeclaredMethods();

        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{proxyInterfaceClass});
        MethodInterceptor[] methodInterceptors = new MethodInterceptor[proxyDeclaredMethods.length];
        int idx = 0;
        for (final Method declaredMethod : proxyDeclaredMethods) {
            MethodInterceptor methodInterceptor = new MethodInterceptor() {
                @Override
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    if (!declaredMethod.equals(method)) {
                        return methodProxy.invokeSuper(o, objects);
                    }
                    for (Method localServiceDeclaredMethod : targetDeclaredMethods) {
                        if (essentiallyEqualMethods(localServiceDeclaredMethod, declaredMethod)) {
                            return org.springframework.util.ReflectionUtils.invokeMethod(localServiceDeclaredMethod,
                                    targetClass, objects);
                        }
                    }
                    throw new UnsupportedOperationException("Method is not supported");
                }
            };
            methodInterceptors[idx++] = methodInterceptor;
        }
        enhancer.setCallbacks(methodInterceptors);
        enhancer.setNamingPolicy(new NamingPolicy() {
            @Override
            public String getClassName(String name, String enhancerName, Object o, Predicate predicate) {
                String packageName = name.substring(0, name.lastIndexOf(".") + 1);
                return packageName + Utils.getProxyImplementationName(proxyInterfaceClass.getSimpleName());
            }
        });
        return enhancer.create();
    }

    private boolean essentiallyEqualMethods(Method method1, Method method2) {
        // it's needed that methods have the same name and parameters types
        boolean equalNames = method1.getName().equals(method2.getName());
        boolean equalReturnTypes = method1.getGenericReturnType().equals(method2.getGenericReturnType());
        boolean equalParameters = equalParameterTypes(method1, method2);
        return equalNames && equalReturnTypes && equalParameters;
    }

    private boolean equalParameterTypes(Method method1, Method method2) {
        Class<?>[] parameterTypes = method1.getParameterTypes();
        Class<?>[] declaredParameterTypes = method2.getParameterTypes();
        if (parameterTypes.length != declaredParameterTypes.length) {
            return false;
        }
        for (Class parameterType : parameterTypes) {
            boolean hasExpectedParameterType = false;
            for (Class declaredParameterType : declaredParameterTypes) {
                if (parameterType.equals(declaredParameterType)) {
                    hasExpectedParameterType = true;
                    break;
                }
            }
            if (!hasExpectedParameterType) {
                return false;
            }
        }
        return true;
    }
}
