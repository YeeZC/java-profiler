package me.zyee.profiler.agent.core.advice;

import java.util.HashSet;
import java.util.Set;
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
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        final MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        final String signCode = getBehaviorSignCode(name, descriptor);
        if (!isMatchedBehavior(signCode)) {
            return mv;
        }

        return new AdviceAdapter(Opcodes.ASM7,
                new JSRInlinerAdapter(mv, access, name, descriptor, signature, exceptions),
                access, name, descriptor) {

            private final Label beginLabel = new Label();
            private final Label endLabel = new Label();
            private final Type ASM_TYPE_CLASS = Type.getType(Class.class);
            private final Type ASM_TYPE_OBJECT = Type.getType(Object.class);
            private final Type ASM_TYPE_SPY = Type.getType(Spy.class);
            private final Type ASM_TYPE_THROWABLE = Type.getType(Throwable.class);
            private final Type ASM_TYPE_INTEGER = Type.getType(Integer.class);
            private final Type ASM_TYPE_STRING = Type.getType(String.class);
            private final Type ASM_TYPE_INT = Type.getType(int.class);
            private final Type ASM_TYPE_CLASS_LOADER = Type.getType(ClassLoader.class);
            private final Type ASM_TYPE_METHOD = Type.getType(java.lang.reflect.Method.class);
            private final Type ASM_TYPE_OBJECT_ARRAY = Type.getType(Object[].class);
            private final Method ASM_METHOD_METHOD_INVOKE = Method.getMethod("Object invoke(Object,Object[])");
            private final CodeLock codeLockForTracing = new TracingAsmCodeLock(this);

            @Override
            protected void onMethodEnter() {
                codeLockForTracing.lock(() -> {
                    getStatic(ASM_TYPE_SPY, "ON_BEFORE", ASM_TYPE_METHOD);
                    // method.invoke(null, ...)
                    pushNull();
                    // 推入before方法的参数
                    loadBeforeArgs();
                    invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
                    pop();
                });
                mark(beginLabel);
            }

            private void loadBeforeArgs() {
                push(Spy.ON_BEFORE.getParameterCount());
                newArray(ASM_TYPE_OBJECT);

                dup();
                push(0);
                push(adviceId);
                box(ASM_TYPE_INT);
                arrayStore(ASM_TYPE_INTEGER);

                dup();
                push(1);
                loadClassLoader();
                arrayStore(ASM_TYPE_CLASS_LOADER);

                dup();
                push(2);
                push(targetJavaClassName);
                arrayStore(ASM_TYPE_STRING);

                dup();
                push(3);
                push(name);
                arrayStore(ASM_TYPE_STRING);

                dup();
                push(4);
                push(descriptor);
                arrayStore(ASM_TYPE_STRING);

                dup();
                push(5);
                loadThisOrPushNullIfIsStatic();
                arrayStore(ASM_TYPE_OBJECT);

                dup();
                push(6);
                loadArgArray();
                arrayStore(ASM_TYPE_OBJECT_ARRAY);
            }

            @Override
            protected void onMethodExit(int opcode) {
                codeLockForTracing.lock(() -> {
                    if (!isThrow(opcode)) {
                        loadReturn(opcode);
                        getStatic(ASM_TYPE_SPY, "ON_RETURN", ASM_TYPE_METHOD);
                        pushNull();
                        loadExit(ASM_TYPE_OBJECT);
                        invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
                        pop();
                    }
                });
            }

            @Override
            public void visitMaxs(int maxStack, int maxLocals) {
                mark(endLabel);
                visitTryCatchBlock(beginLabel, endLabel, mark(),
                        ASM_TYPE_THROWABLE.getInternalName());

                codeLockForTracing.lock(() -> {
                    // 加载Throwable
                    dup();
                    getStatic(ASM_TYPE_SPY, "ON_THROWS", ASM_TYPE_METHOD);
                    pushNull();
                    loadExit(ASM_TYPE_THROWABLE);
                    invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
                    pop();
                });

                throwException();

                super.visitMaxs(maxStack, maxLocals);
            }

            private void loadExit(Type store) {
                dup2X1();
                pop2();
                push(2);
                newArray(ASM_TYPE_OBJECT);
                dup();

                push(0);
                push(adviceId);
                box(ASM_TYPE_INT);
                arrayStore(ASM_TYPE_INTEGER);
                dup();

                dup2X1();
                pop2();
                push(1);
                swap();
                arrayStore(store);
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
             * 是否抛出异常返回(通过字节码判断)
             * @param opcode 操作码
             * @return true:以抛异常形式返回 / false:非抛异常形式返回(return)
             */
            private boolean isThrow(int opcode) {
                return opcode == ATHROW;
            }

        };
    }

}
