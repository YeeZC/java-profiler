package me.zyee.profiler.agent.core.advice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.zyee.profiler.agent.core.utils.AsmMethods;
import me.zyee.profiler.spy.Spy;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.commons.Method;

import static me.zyee.profiler.agent.core.utils.AgentStringUtils.toJavaClassName;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/7
 */
public class AdviceWeaver extends ClassVisitor implements Opcodes {
    private final Set<String> signCodes;
    private final int adviceId;
    private final String targetJavaClassName;
    private String superName;
    private boolean isMethodEnter = false;

    public AdviceWeaver(int adviceId, Set<String> signCodes, String targetJavaClassName, ClassVisitor cv) {
        super(Opcodes.ASM7, cv);
        this.adviceId = adviceId;
        this.signCodes = new HashSet<>(signCodes);
        this.targetJavaClassName = targetJavaClassName;
    }

    private String getBehaviorSignCode(final String name,
                                       final String desc) {
        final StringBuilder sb = new StringBuilder(256).append(targetJavaClassName).append("#").append(name).append("(");

        final Type[] methodTypes = Type.getMethodType(desc).getArgumentTypes();
        if (methodTypes.length != 0) {
            sb.append(methodTypes[0].getClassName());
            for (int i = 1; i < methodTypes.length; i++) {
                sb.append(",").append(methodTypes[i].getClassName());
            }
        }

        return sb.append(")").toString();
    }

    private boolean isMatchedBehavior(final String signCode) {
        return signCodes.contains(signCode.replace("/", "."));
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.superName = superName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        final MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        final String signCode = getBehaviorSignCode(name, descriptor);
        if (!isMatchedBehavior(signCode)) {
            return mv;
        }

        return new AdviceAdapter(Opcodes.ASM7,
                new JSRInlinerAdapter(mv, access, name, descriptor, signature, exceptions),
                access, name, descriptor) {

            final List<AsmTryCatchBlock> tcbs = new ArrayList<>();

            private final Label beginLabel = new Label();
            private final Label endLabel = new Label();
            private final Type ASM_TYPE_CLASS = Type.getType(Class.class);
            private final Type ASM_TYPE_OBJECT = Type.getType(Object.class);
            private final Type ASM_TYPE_SPY = Type.getType(Spy.class);
            private final Type ASM_TYPE_ASM_METHODS = Type.getType(AsmMethods.class);
            private final Type ASM_TYPE_THROWABLE = Type.getType(Throwable.class);
            private final Type ASM_TYPE_INTEGER = Type.getType(Integer.class);
            private final Type ASM_TYPE_STRING = Type.getType(String.class);
            private final Type ASM_TYPE_INT = Type.getType(int.class);
            private final Type ASM_TYPE_METHOD = Type.getType(java.lang.reflect.Method.class);
            private final Method ASM_METHOD_METHOD_INVOKE = Method.getMethod("Object invoke(Object,Object[])");
            private final Type[] argumentTypeArray = Type.getArgumentTypes(descriptor);
            private final CodeLock codeLockForTracing = new TracingAsmCodeLock(this);
            private int lineNumber;

            @Override
            protected void onMethodEnter() {
                codeLockForTracing.lock(() -> {
                    mark(beginLabel);
                    push(adviceId);
                    loadClassLoader();
                    push(targetJavaClassName);
                    push(name);
                    push(descriptor);
                    loadThisOrPushNullIfIsStatic();
                    loadArgArray();
                    dup();
                    invokeStatic(ASM_TYPE_SPY, AsmMethods.ON_BEFORE);
                    swap();
                    storeArgArray();
                    pop();
                    isMethodEnter = true;
                });
            }

            @Override
            protected void onMethodExit(int opcode) {
                codeLockForTracing.lock(() -> {
                    if (!isThrow(opcode)) {
                        push(adviceId);
                        loadReturn(opcode);
                        invokeStatic(ASM_TYPE_SPY, AsmMethods.ON_RETURN);
                    }
                });

            }

            @Override
            public void visitMaxs(int maxStack, int maxLocals) {
                mark(endLabel);
//              catchException(beginLabel, endLabel, ASM_TYPE_THROWABLE);
                visitTryCatchBlock(beginLabel, endLabel, mark(),
                        ASM_TYPE_THROWABLE.getInternalName());

                codeLockForTracing.lock(() -> {
                    push(adviceId);
//                  loadThrow();
                    dup();
                    invokeStatic(ASM_TYPE_SPY, AsmMethods.ON_THROWS);
                });

                throwException();

                super.visitMaxs(maxStack, maxLocals);
            }

            @Override
            public void visitLineNumber(int line, Label start) {
                super.visitLineNumber(line, start);
                lineNumber = line;
            }

            private void loadArrayForInvokeTracing(String owner, String name, String desc, int lineNumber) {
                push(5);
                newArray(ASM_TYPE_OBJECT);

                dup();
                push(0);
                push(adviceId);
                box(ASM_TYPE_INT);
                arrayStore(ASM_TYPE_INTEGER);

                dup();
                push(1);
                push(owner);
                arrayStore(ASM_TYPE_STRING);

                dup();
                push(2);
                push(name);
                arrayStore(ASM_TYPE_STRING);

                dup();
                push(3);
                push(desc);
                arrayStore(ASM_TYPE_STRING);

                dup();
                push(4);
                push(lineNumber);
                box(ASM_TYPE_INT);
                arrayStore(ASM_TYPE_INTEGER);
            }

            @Override
            public void visitInsn(int opcode) {
                super.visitInsn(opcode);
                codeLockForTracing.code(opcode);
            }

            @Override
            public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                tcbs.add(new AsmTryCatchBlock(start, end, handler, type));
            }

            @Override
            public void visitEnd() {
                for (AsmTryCatchBlock tcb : tcbs) {
                    super.visitTryCatchBlock(tcb.start, tcb.end, tcb.handler, tcb.type);
                }

                super.visitEnd();
            }

            /*
             * 跟踪代码
             */
            private void tracing(Method method, final String owner, final String name, final String desc, final int lineNumber) {

                codeLockForTracing.lock(() -> {

                    getStatic(ASM_TYPE_ASM_METHODS, method.getName(), ASM_TYPE_METHOD);
                    pushNull();
                    loadArrayForInvokeTracing(owner, name, desc, lineNumber);

                    invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
                    pop();

                });

            }

            protected boolean isSuperOrSiblingConstructorCall(int opcode, String owner, String name) {
                return (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")
                        && (superName.equals(owner) || targetJavaClassName.equals(owner)));
            }

            @Override
            public void visitMethodInsn(int opcode, final String owner, final String name, final String desc, boolean itf) {
                if (isSuperOrSiblingConstructorCall(opcode, owner, name)) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    return;
                }

                // 如果CALL事件没有启用，则不需要对CALL进行增强
                // 如果正在CALL的方法来自于SANDBOX本身，则不需要进行追踪
                if (!isMethodEnter || codeLockForTracing.isLock()) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    return;
                }


                // 方法调用前通知
                tracing(AsmMethods.ON_BEFORE, owner, name, desc, lineNumber);

                final Label beginLabel = new Label();
                final Label endLabel = new Label();
                final Label finallyLabel = new Label();

                // try
                // {

                mark(beginLabel);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                mark(endLabel);

                // 方法调用后通知
                tracing(AsmMethods.ON_RETURN, owner, name, desc, lineNumber);
                goTo(finallyLabel);

                // }
                // catch
                // {

                catchException(beginLabel, endLabel, ASM_TYPE_THROWABLE);
                tracing(AsmMethods.ON_THROWS, owner, name, desc, lineNumber);

                throwException();

                // }
                // finally
                // {
                mark(finallyLabel);
                // }
            }

            /**
             * 加载返回值
             * @param opcode 操作吗
             */
            private void loadReturn(int opcode) {
                switch (opcode) {

                    case RETURN: {
                        pushNull();
                        break;
                    }

                    case ARETURN: {
                        dup();
                        break;
                    }

                    case LRETURN:
                    case DRETURN: {
                        dup2();
                        box(Type.getReturnType(methodDesc));
                        break;
                    }

                    default: {
                        dup();
                        box(Type.getReturnType(methodDesc));
                        break;
                    }

                }
            }

            private void pushNull() {
                push((Type) null);
            }

            /**
             * 是否静态方法
             * @return true:静态方法 / false:非静态方法
             */
            private boolean isStaticMethod() {
                return (methodAccess & ACC_STATIC) != 0;
            }

            /**
             * 加载ClassLoader<br/>
             * 这里分开静态方法中ClassLoader的获取以及普通方法中ClassLoader的获取
             * 主要是性能上的考虑
             */
            private void loadClassLoader() {

                if (this.isStaticMethod()) {
                    visitLdcInsn(toJavaClassName(targetJavaClassName));
                    invokeStatic(ASM_TYPE_CLASS, Method.getMethod("Class forName(String)"));

                } else {
                    loadThis();
                    invokeVirtual(ASM_TYPE_OBJECT, Method.getMethod("Class getClass()"));
                }
                invokeVirtual(ASM_TYPE_CLASS, Method.getMethod("ClassLoader getClassLoader()"));

            }

            private void loadThisOrPushNullIfIsStatic() {
                if (isStaticMethod()) {
                    pushNull();
                } else {
                    loadThis();
                }
            }

            /**
             * 保存参数数组
             */
            final protected void storeArgArray() {
                for (int i = 0; i < argumentTypeArray.length; i++) {
                    dup();
                    push(i);
                    arrayLoad(ASM_TYPE_OBJECT);
                    unbox(argumentTypeArray[i]);
                    storeArg(i);
                }
            }

            /**
             * 是否抛出异常返回(通过字节码判断)
             * @param opcode 操作码
             * @return true:以抛异常形式返回 / false:非抛异常形式返回(return)
             */
            private boolean isThrow(int opcode) {
                return opcode == ATHROW;
            }

        };
    }

    static class AsmTryCatchBlock {
        Label start;
        Label end;
        Label handler;
        String type;

        AsmTryCatchBlock(Label start, Label end, Label handler, String type) {
            this.start = start;
            this.end = end;
            this.handler = handler;
            this.type = type;
        }
    }


}
