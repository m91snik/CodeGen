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

    public Object generate(final Object targetClass, final Class proxyInterfaceClass) {

        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{proxyInterfaceClass});

        MethodInterceptor[] methodInterceptors = createMethodInterceptors(targetClass, proxyInterfaceClass);

        enhancer.setCallbacks(methodInterceptors);
        NamingPolicy namingPolicy = createNamingPolicy(proxyInterfaceClass);
        enhancer.setNamingPolicy(namingPolicy);
        return enhancer.create();
    }

    private MethodInterceptor[] createMethodInterceptors(final Object targetClass, Class proxyInterfaceClass) {
        Method[] proxyDeclaredMethods = proxyInterfaceClass.getDeclaredMethods();
        final Method[] targetDeclaredMethods = targetClass.getClass().getDeclaredMethods();
        MethodInterceptor[] methodInterceptors = new MethodInterceptor[proxyDeclaredMethods.length];
        for (int idx = 0; idx < proxyDeclaredMethods.length; idx++) {
            MethodInterceptor methodInterceptor =
                    createMethodInterceptor(targetClass, targetDeclaredMethods, proxyDeclaredMethods[idx]);
            methodInterceptors[idx++] = methodInterceptor;
        }
        return methodInterceptors;
    }

    private MethodInterceptor createMethodInterceptor(final Object targetClass, final Method[] targetDeclaredMethods,
                                                      final Method declaredMethod) {
        return new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
                    throws Throwable {
                if (!declaredMethod.equals(method)) {
                    return methodProxy.invokeSuper(o, objects);
                }
                for (Method localServiceDeclaredMethod : targetDeclaredMethods) {
                    if (Utils.essentiallyEqualMethods(localServiceDeclaredMethod, declaredMethod)) {
                        return org.springframework.util.ReflectionUtils.invokeMethod(localServiceDeclaredMethod,
                                targetClass, objects);
                    }
                }
                throw new IllegalStateException("Method is not supported");
            }
        };
    }



    private NamingPolicy createNamingPolicy(final Class proxyInterfaceClass) {
        return new NamingPolicy() {
            @Override
            public String getClassName(String name, String enhancerName, Object o, Predicate predicate) {
                String packageName = name.substring(0, name.lastIndexOf(".") + 1);
                return packageName + Utils.getProxyImplementationName(proxyInterfaceClass.getSimpleName());
            }
        };
    }

}
