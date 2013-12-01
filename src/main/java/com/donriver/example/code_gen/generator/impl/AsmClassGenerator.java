/**
 * Created by Nikolay Garbuzov on 13.10.13.
 */
package com.donriver.example.code_gen.generator.impl;

import com.donriver.example.code_gen.util.Utils;
import org.objectweb.asm.*;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AsmClassGenerator {

    public Class generate(Class targetClass, Class proxyInterfaceClass) throws Exception {
        //Assume that target has only one interface
        Class targetInterfaceClass = targetClass.getInterfaces()[0];

        String asmProxyName = Type.getInternalName(proxyInterfaceClass);
        final String proxyImplName = asmProxyName + "Impl";

        ClassWriter rawClassWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(rawClassWriter, new PrintWriter(System.out));
        CheckClassAdapter classWriter = new CheckClassAdapter(traceClassVisitor);

        classWriter.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, proxyImplName, null,
                          Type.getInternalName(Object.class), new String[]{asmProxyName});

        for (Annotation annotation : targetClass.getAnnotations()) {
            Class<? extends Annotation> annotationClass = annotation.annotationType();

            AnnotationVisitor annotationVisitor =
                    classWriter.visitAnnotation(Type.getDescriptor(annotationClass), true);
            fillAnnotationByValues(annotationVisitor, annotation);
            annotationVisitor.visitEnd();
        }

        createTargetField(targetInterfaceClass, classWriter);

        createDefaultConstructor(classWriter);

        addProxyMethods(targetClass, targetInterfaceClass, proxyInterfaceClass, proxyImplName, classWriter);

        classWriter.visitEnd();

        final byte[] bytes = rawClassWriter.toByteArray();

        final ClassLoader classLoader = targetClass.getClassLoader();

        return Utils.defineClass(proxyImplName.replace("/", "."), bytes, classLoader);
    }

    private void addProxyMethods(Class targetClass, Class targetInterfaceClass, Class proxyInterfaceClass,
                                 String proxyImplName, CheckClassAdapter classWriter) throws NoSuchFieldException,
            InvocationTargetException, IllegalAccessException {
        for (Method method : targetClass.getDeclaredMethods()) {
            boolean found = false;
            for (Method expectedMethod : proxyInterfaceClass.getDeclaredMethods()) {
                if (expectedMethod.getName().equals(method.getName())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                createProxyMethod(targetInterfaceClass, method, proxyImplName, classWriter);
            }
        }
    }

    private void createProxyMethod(Class targetInterfaceClass, Method method, String proxyImplName, CheckClassAdapter classWriter) throws
            NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        String returnTypeDescriptor = Type.getDescriptor(method.getReturnType());

        String methodDescriptor = buildMethodDescriptor(method, returnTypeDescriptor);

        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, method.getName(),
                                                              methodDescriptor, null, null);

        addMethodAnnotations(method, methodVisitor);

        methodVisitor.visitCode();

        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, proxyImplName, "target", Type.getDescriptor(
                targetInterfaceClass));

        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);

        methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getInternalName(targetInterfaceClass),
                                      method.getName(), methodDescriptor);

        methodVisitor.visitInsn(Opcodes.ARETURN);
        methodVisitor.visitMaxs(3, 3);
        methodVisitor.visitEnd();
    }

    private void addMethodAnnotations(Method method, MethodVisitor methodVisitor) throws IllegalAccessException,
            InvocationTargetException {
        for (Annotation annotation : method.getAnnotations()) {
            Class<? extends Annotation> annotationClass = annotation.annotationType();

            AnnotationVisitor annotationVisitor =
                    methodVisitor.visitAnnotation(Type.getDescriptor(annotationClass), true);
            fillAnnotationByValues(annotationVisitor, annotation);
            annotationVisitor.visitEnd();
        }
    }

    private void fillAnnotationByValues(AnnotationVisitor annotationVisitor, Annotation annotation) throws
            IllegalAccessException, InvocationTargetException {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        for (Method annotationMethod : annotationClass.getDeclaredMethods()) {
            Class<?> returnType = annotationMethod.getReturnType();
            Object annotationMethodValue = annotationMethod.invoke(annotation);
            if (returnType.isEnum()) {
                annotationVisitor.visitEnum(annotationMethod.getName(), Type.getDescriptor(returnType),
                                            annotationMethodValue.toString());
            } else if (returnType.isArray()) {
                AnnotationVisitor arrayVisitor = annotationVisitor.visitArray(annotationMethod.getName());
                for (int i = 0; i < Array.getLength(annotationMethodValue); i++) {
                    arrayVisitor.visit(null, Array.get(annotationMethodValue, i));
                }
                arrayVisitor.visitEnd();
            } else if (returnType.isAnnotation()) {
                //not fully implemented
                annotationVisitor.visitAnnotation(annotationMethod.getName(), Type.getDescriptor(returnType));
            } else {
                annotationVisitor.visit(annotationMethod.getName(), annotationMethodValue);
            }
        }
    }

    private String buildMethodDescriptor(Method method, String returnTypeDescriptor) {
        StringBuilder methodDescriptor = new StringBuilder("(");
        for (Class clazz : method.getParameterTypes()) {
            methodDescriptor.append(Type.getDescriptor(clazz));
        }
        methodDescriptor.append(")");
        return methodDescriptor.append(returnTypeDescriptor).toString();
    }

    private void createDefaultConstructor(CheckClassAdapter classWriter) {
        MethodVisitor constr = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>",
                                                       "()" + Type.VOID_TYPE.getDescriptor(), null, null);
        constr.visitCode();
        constr.visitVarInsn(Opcodes.ALOAD, 0);
        constr.visitMethodInsn(Opcodes.INVOKESPECIAL,
                               Type.getInternalName(Object.class), "<init>", "()" + Type.VOID_TYPE.getDescriptor());
        constr.visitInsn(Opcodes.RETURN);
        constr.visitMaxs(1, 1);
        constr.visitEnd();
    }

    private void createTargetField(Class targetInterfaceClass, CheckClassAdapter classWriter) {
        FieldVisitor targetVisitor =
                classWriter.visitField(Opcodes.ACC_PRIVATE, "target", Type.getDescriptor(
                        targetInterfaceClass), null, null);
        targetVisitor.visitAnnotation(Type.getDescriptor(Autowired.class), true).visitEnd();
        targetVisitor.visitEnd();
    }
}
