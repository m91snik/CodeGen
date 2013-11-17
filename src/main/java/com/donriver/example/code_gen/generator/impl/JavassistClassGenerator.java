/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 9/20/13
 * Created by: Nikolay Garbuzov
 */
package com.donriver.example.code_gen.generator.impl;

import com.donriver.example.code_gen.annotation.GenClassAnnotation;
import com.donriver.example.code_gen.annotation.GenEnum;
import com.donriver.example.code_gen.annotation.GenMethodAnnotation;
import com.donriver.example.code_gen.util.Utils;
import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;

public class JavassistClassGenerator {

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
            ClassNotFoundException {
        ClassPool pool = getClassPool(proxyInterfaceClass);

        CtClass proxyImplCtClass = createProxyImplCtClass(proxyInterfaceClass, pool);

        addTargetField(targetClass, pool, proxyImplCtClass);

        addProxyMethods(proxyInterfaceClass, pool, proxyImplCtClass);

        addClassAnnotations(targetClass, pool, proxyImplCtClass);

        return convertToClass(proxyImplCtClass);
    }

    private ClassPool getClassPool(Class proxyInterfaceClass) {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(proxyInterfaceClass));
        return pool;
    }

    private void addClassAnnotations(Class targetClass, ClassPool pool, CtClass proxyImplCtClass)
            throws NotFoundException {
        ClassFile classFile = proxyImplCtClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();

        AnnotationsAttribute annotationsAttribute =
                new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);

        Annotation annotation = generateGenClassAnnotation(targetClass, pool, constPool);

        annotationsAttribute.addAnnotation(annotation);
        classFile.addAttribute(annotationsAttribute);
        classFile.setVersionToJava5();
    }

    private Annotation generateGenClassAnnotation(Class targetClass, ClassPool pool, ConstPool constPool)
            throws NotFoundException {
        Annotation annotation = new Annotation(constPool, pool.get(GenClassAnnotation.class.getName()));
        GenClassAnnotation classAnnotation = (GenClassAnnotation) targetClass.getAnnotation
                (GenClassAnnotation.class);
        annotation.addMemberValue("serviceName", new StringMemberValue(classAnnotation.serviceName(), constPool));
        ArrayMemberValue loggingChannels = new ArrayMemberValue(constPool);
        MemberValue[] elements = new MemberValue[classAnnotation.loggingChannels().length];
        for (int idx = 0; idx < elements.length; idx++) {
            String channelName = classAnnotation.loggingChannels()[0];
            elements[idx] = new StringMemberValue(channelName, constPool);
        }
        loggingChannels.setValue(elements);
        annotation.addMemberValue("loggingChannels", loggingChannels);
        return annotation;
    }

    private void addProxyMethods(Class<?> proxyInterfaceClass, ClassPool pool, CtClass proxyImplCtClass)
            throws NotFoundException, CannotCompileException {
        for (Method method : proxyInterfaceClass.getDeclaredMethods()) {
            CtMethod ctMethod = createProxyMethod(pool, proxyImplCtClass, method);

            addMethodAnnotations(ctMethod);

            proxyImplCtClass.addMethod(ctMethod);
        }
    }

    private void addMethodAnnotations(CtMethod ctMethod) {
        //NOTE: you can add only Runtime annotations
        MethodInfo methodInfo = ctMethod.getMethodInfo();

        ConstPool constPool = methodInfo.getConstPool();
        AnnotationsAttribute annotationsAttribute =
                new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annotation = generateGenMethodAnnotation(constPool);
        annotationsAttribute.addAnnotation(annotation);

        methodInfo.addAttribute(annotationsAttribute);
    }

    private Annotation generateGenMethodAnnotation(ConstPool constPool) {
        EnumMemberValue enumMemberValue = new EnumMemberValue(constPool);
        enumMemberValue.setType(GenEnum.class.getName());
        enumMemberValue.setValue(GenEnum.SECOND.name());
        //NOTE: it's needed to use this constructor. Annotation(ConstPool cp, CtClass clazz) not supports enums
        Annotation annotation = new Annotation(GenMethodAnnotation.class.getName(), constPool);
        annotation.addMemberValue("genEnum", enumMemberValue);
        return annotation;
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

    /**
     * Converts {@link javassist.CtClass} to {@link java.lang.Class}
     *
     * @param proxyImplCtClass
     * @return
     * @throws CannotCompileException
     */
    private Class convertToClass(CtClass proxyImplCtClass) throws CannotCompileException {
        Thread thread = Thread.currentThread();
        ClassLoader loader = thread.getContextClassLoader();
        ProtectionDomain protectionDomain = thread.getClass().getProtectionDomain();

        return proxyImplCtClass.toClass(loader, protectionDomain);
    }
}
