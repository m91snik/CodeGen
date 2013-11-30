/**
 * Created by m91snik on 02.10.13.
 */
package com.donriver.example.code_gen.util;

import com.google.common.collect.Multimap;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.AbstractScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;

public class Utils {

    private static final String DEFINE_CLASS = "defineClass";
    private static final Class[] PARAMETER_TYPES = new Class[]{String.class, byte[].class, int.class, int.class};

    public static String getProxyImplementationName(String proxyInterfaceName) {
        return proxyInterfaceName + "Impl";
    }

    /**
     * Return full class name by simple class name only for classes in current class path
     *
     * @param className
     * @return full class name
     * @throws ClassNotFoundException
     */
    public static Class getClassBySimpleName(final String className) throws ClassNotFoundException {
        AbstractScanner scannerByName = new AbstractScanner() {

            @Override
            public void scan(Object cls) {
                final String classNameCandidate = getMetadataAdapter().getClassName(cls);
                if (classNameCandidate.endsWith(className)) {
                    getStore().put(classNameCandidate, classNameCandidate);
                }
            }
        };
        Reflections reflections = new Reflections("", scannerByName);
        Multimap<String, String> foundFacadeNames = reflections.getStore().get(scannerByName.getClass());
        for (String foundFacadeName : foundFacadeNames.keySet()) {
            return ReflectionUtils.forName(foundFacadeName, Thread.currentThread().getContextClassLoader());
        }
        throw new ClassNotFoundException("Cannot find appropriate class for " + className);
    }

    public static Class toClass(String name, byte[] bytes, ClassLoader classLoader) throws java.security.PrivilegedActionException, IllegalAccessException, InvocationTargetException {
        Method defineClass = Utils.getDefineClassMethod(classLoader);
        defineClass.setAccessible(true);

        Object result = defineClass.invoke(classLoader, name, bytes, 0, bytes.length);
        defineClass.setAccessible(true);
        return (Class) result;
    }

    private static Method getDefineClassMethod(final ClassLoader classLoader) throws java.security.PrivilegedActionException {
        return (Method) AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws Exception {
                Class aClass = findNearestClassWithDefineClassMethod(classLoader.getClass());
                return aClass.getDeclaredMethod(DEFINE_CLASS, PARAMETER_TYPES);
            }

            private Class findNearestClassWithDefineClassMethod(Class clz) {
                for (Method m : clz.getDeclaredMethods()) {
                    if (DEFINE_CLASS.equals(m.getName())) {
                        if (Arrays.equals(PARAMETER_TYPES, m.getParameterTypes())) {
                            return clz;
                        }
                    }
                }
                return findNearestClassWithDefineClassMethod(clz.getSuperclass());
            }
        });
    }
}
