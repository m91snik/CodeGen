/**
 * Created by m91snik on 02.10.13.
 */
package com.donriver.example.code_gen.util;

import com.google.common.collect.Multimap;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.AbstractScanner;

public class Utils {

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
}
