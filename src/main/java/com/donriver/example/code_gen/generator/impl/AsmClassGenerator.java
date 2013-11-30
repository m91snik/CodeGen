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

        createTargetField(targetInterfaceClass, classWriter);

        createDefaultConstructor(classWriter);

        createGetTargetMethod(targetInterfaceClass, proxyImplName, classWriter);

//        StringBuilder invokeTargetDescriptor = new StringBuilder("(");
//        Method doTestRequest = proxyInterfaceClass.getDeclaredMethod("doTestRequest");
//        for (Class clazz : doTestRequest.getParameterTypes()) {
//            invokeTargetDescriptor.append(Type.getDescriptor(clazz) + ",");
//        }
//        invokeTargetDescriptor.replace(invokeTargetDescriptor.length() - 1, invokeTargetDescriptor.length(), ")");
//        invokeTargetDescriptor.append(Type.getDescriptor(doTestRequest.getReturnType()));
//
//        MethodVisitor invokeTarget = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "invokeTarget",
//                                                             invokeTargetDescriptor.toString(), null, null);
//        invokeTarget.visitCode();
//        invokeTarget.visitVarInsn(Opcodes.ALOAD, 0);
//        invokeTarget.visitFieldInsn(Opcodes.GETFIELD, proxyImplName, "value", Type.BOOLEAN_TYPE.getDescriptor());
//        invokeTarget.visitInsn(Opcodes.IRETURN);
//        invokeTarget.visitMaxs(0, 0);
//        invokeTarget.visitEnd();

        classWriter.visitEnd();

        final byte[] bytes = rawClassWriter.toByteArray();

        final ClassLoader classLoader = targetClass.getClassLoader();

        return Utils.toClass(proxyImplName.replace("/", "."), bytes, classLoader);
    }


    private void createGetTargetMethod(Class targetInterfaceClass, String proxyImplName, CheckClassAdapter classWriter) {
        MethodVisitor getTarget = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "getTarget",
                                                          "()" + Type.getDescriptor(targetInterfaceClass),
                                                          null, null);
        getTarget.visitCode();
        getTarget.visitVarInsn(Opcodes.ALOAD, 0);
        getTarget.visitFieldInsn(Opcodes.GETFIELD, proxyImplName, "target", Type.getDescriptor(
                targetInterfaceClass));
        getTarget.visitInsn(Opcodes.ARETURN);
        getTarget.visitMaxs(1, 1);
        getTarget.visitEnd();
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
