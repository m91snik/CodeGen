/**
 * Copyright m91snik
 * Created on: 9/20/13
 * Created by: m91snik
 */
package com.m91snik.code_gen.generator.impl;

import com.m91snik.code_gen.util.AnnotationResolver;
import com.m91snik.code_gen.util.Utils;
import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedActionException;

public class JavassistClassGenerator {

    @Autowired
    private AnnotationResolver annotationResolver;

    public static final String TARGET_INSTANCE_NAME = "target";

    /**
     * Generate proxy impl class be proxy interface and target class.
     *
     * @param targetClass
     * @param proxyInterfaceClass
     * @return
     * @throws CannotCompileException
     * @throws InstantiationException
     * @throws NotFoundException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     */
    public Class generate(Class targetClass, Class proxyInterfaceClass) throws CannotCompileException,
            InstantiationException, NotFoundException, IllegalAccessException, NoSuchMethodException,
            ClassNotFoundException, PrivilegedActionException, IOException, InvocationTargetException {
        ClassPool pool = getClassPool(proxyInterfaceClass);

        CtClass proxyImplCtClass = createProxyImplCtClass(proxyInterfaceClass, pool);

        addClassAnnotations(targetClass, proxyImplCtClass);

        addTargetField(targetClass, pool, proxyImplCtClass);

        addProxyMethods(targetClass, pool, proxyImplCtClass);

        return Utils.defineClass(proxyImplCtClass.getName(), proxyImplCtClass.toBytecode(),
                                 targetClass.getClassLoader());
    }

    private ClassPool getClassPool(Class proxyInterfaceClass) {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(proxyInterfaceClass));
        return pool;
    }

    private void addClassAnnotations(Class targetClass, CtClass proxyImplCtClass)
            throws NotFoundException, InvocationTargetException, IllegalAccessException {
        ClassFile classFile = proxyImplCtClass.getClassFile();
        classFile.setVersionToJava5();

        ConstPool constPool = classFile.getConstPool();
        AnnotationsAttribute annotationsAttribute =
                new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        for (java.lang.annotation.Annotation targetAnnotation : targetClass.getAnnotations()) {
            if (annotationResolver.ignoreAnnotation(targetAnnotation)) {
                continue;
            }
            Annotation annotation = generateAnnotation(targetAnnotation, constPool);
            annotationsAttribute.addAnnotation(annotation);
        }
        classFile.addAttribute(annotationsAttribute);
    }

    private Annotation generateAnnotation(java.lang.annotation.Annotation targetAnnotation, ConstPool constPool)
            throws NotFoundException, InvocationTargetException, IllegalAccessException {
        //NOTE: it's needed to use this constructor. Annotation(ConstPool cp, CtClass clazz) not supports enums
        Annotation annotation = new Annotation(targetAnnotation.annotationType().getName(), constPool);
        for (Method targetAnnotationMethod : targetAnnotation.annotationType().getDeclaredMethods()) {
            Class<?> returnType = targetAnnotationMethod.getReturnType();
            Object annotationMethodValue = targetAnnotationMethod.invoke(targetAnnotation);
            MemberValue memberValue = resolveAnnotationMemberValue(constPool, returnType, annotationMethodValue);
            annotation.addMemberValue(targetAnnotationMethod.getName(), memberValue);
        }
        return annotation;
    }

    //TODO: just show case. for practical purposes it should be implemented in strategies instead of ifs
    private MemberValue resolveAnnotationMemberValue(ConstPool constPool, Class<?> annotationMemberType, Object annotationMethodValue) {
        if (String.class.equals(annotationMemberType)) {
            return new StringMemberValue((String) annotationMethodValue, constPool);
        } else if (annotationMemberType.isArray()) {
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constPool);
            MemberValue[] elements = new MemberValue[Array.getLength(annotationMethodValue)];
            for (int idx = 0; idx < elements.length; idx++) {
                Object arrayMemberValueElement = Array.get(annotationMethodValue, idx);
                if (arrayMemberValueElement instanceof String) {
                    elements[idx] = new StringMemberValue((String) arrayMemberValueElement, constPool);
                } else {
                    throw new IllegalArgumentException(annotationMemberType + " is not supported");
                }
            }
            arrayMemberValue.setValue(elements);
            return arrayMemberValue;
        } else if (annotationMemberType.isEnum()) {
            EnumMemberValue enumMemberValue = new EnumMemberValue(constPool);
            enumMemberValue.setType(annotationMemberType.getName());
            enumMemberValue.setValue(annotationMethodValue.toString());
            return enumMemberValue;
        }
        throw new IllegalArgumentException(annotationMemberType + " is not supported");
    }

    private void addProxyMethods(Class<?> targetClass, ClassPool pool, CtClass proxyImplCtClass)
            throws NotFoundException, CannotCompileException, InvocationTargetException, IllegalAccessException {
        for (Method method : targetClass.getDeclaredMethods()) {
            Method targetInterfaceMethod = null;
            for (Method method1 : targetClass.getInterfaces()[0].getDeclaredMethods()) {
                if (Utils.essentiallyEqualMethods(method, method1)) {
                    targetInterfaceMethod = method1;
                    break;
                }
            }
            if (targetInterfaceMethod == null) {
                continue;
            }
            CtMethod ctMethod = createProxyMethod(pool, proxyImplCtClass, targetInterfaceMethod);

            addMethodAnnotations(ctMethod, method);

            proxyImplCtClass.addMethod(ctMethod);
        }
    }

    private void addMethodAnnotations(CtMethod ctMethod, Method method) throws IllegalAccessException,
            NotFoundException,
            InvocationTargetException {
        //NOTE: you can add only Runtime annotations
        MethodInfo methodInfo = ctMethod.getMethodInfo();

        ConstPool constPool = methodInfo.getConstPool();
        AnnotationsAttribute annotationsAttribute =
                new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        for (java.lang.annotation.Annotation targetAnnotation : method.getAnnotations()) {
            if (annotationResolver.ignoreAnnotation(targetAnnotation)) {
                continue;
            }
            Annotation annotation = generateAnnotation(targetAnnotation, constPool);
            annotationsAttribute.addAnnotation(annotation);
        }

        methodInfo.addAttribute(annotationsAttribute);
    }

    private CtMethod createProxyMethod(ClassPool pool, CtClass proxyImplCtClass, Method method)
            throws NotFoundException, CannotCompileException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        CtClass[] parameterCtClasses = new CtClass[parameterTypes.length];
        for (int idx = 0; idx < parameterTypes.length; idx++) {
            Class parameterType = parameterTypes[idx];
            parameterCtClasses[idx] = pool.get(parameterType.getName());
        }
        CtClass returnType = pool.get(method.getReturnType().getName());
        String methodName = method.getName();
        CtMethod ctMethod = new CtMethod(returnType, methodName, parameterCtClasses, proxyImplCtClass);
        ctMethod.setBody("{return " + TARGET_INSTANCE_NAME + "." + methodName + "($$);}");
        return ctMethod;
    }

    private void addTargetField(Class targetClass, ClassPool pool, CtClass proxyImplCtClass)
            throws CannotCompileException, NotFoundException {
        String targetClassName = targetClass.getName();
        CtField targetField = new CtField(pool.get(targetClassName), TARGET_INSTANCE_NAME, proxyImplCtClass);
        FieldInfo fieldInfo = targetField.getFieldInfo();
        ConstPool fieldConstPool = fieldInfo.getConstPool();
        AnnotationsAttribute fieldAnnotationsAttribute =
                new AnnotationsAttribute(fieldConstPool, AnnotationsAttribute.visibleTag);
        Annotation autowiredAnnotation = new Annotation(fieldConstPool, pool.get(Autowired.class.getName()));
        fieldAnnotationsAttribute.addAnnotation(autowiredAnnotation);
        fieldInfo.addAttribute(fieldAnnotationsAttribute);


        proxyImplCtClass.addField(targetField);
    }

    private CtClass createProxyImplCtClass(Class<?> proxyInterfaceClass, ClassPool pool) throws NotFoundException {
        String proxyInterfaceClassName = proxyInterfaceClass.getName();
        String proxyImplClassName = Utils.getProxyImplementationName(proxyInterfaceClassName);
        CtClass proxyImplCtClass = pool.makeClass(proxyImplClassName);
        proxyImplCtClass.addInterface(pool.get(proxyInterfaceClassName));
        return proxyImplCtClass;
    }


}
