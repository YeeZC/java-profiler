package me.zyee.profiler.agent.core.advice;

import java.util.function.Predicate;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.spy.Spy;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static me.zyee.profiler.agent.core.utils.AgentStringUtils.toJavaClassName;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/11
 */
public class ProfilerAdviceAdapter extends AdviceAdapter {
    private final int adviceId;
    private final String targetJavaClassName;
    private final String descriptor;
    private final String superName;
    private final Predicate<Event.Type> eventSwitch;


    ProfilerAdviceAdapter(MethodVisitor methodVisitor,
                          int access,
                          String name,
                          String descriptor,
                          int adviceId,
                          String targetJavaClassName,
                          String superName,
                          Predicate<Event.Type> enable) {
        super(Opcodes.ASM7, methodVisitor, access, name, descriptor);
        this.adviceId = adviceId;
        this.targetJavaClassName = targetJavaClassName;
        this.descriptor = descriptor;
        this.superName = superName;
        this.eventSwitch = enable;
    }

    private final Label beginLabel = new Label();
    private final Label endLabel = new Label();
    private final Type ASM_TYPE_CLASS = Type.getType(Class.class);
    private final Type ASM_TYPE_OBJECT = Type.getType(Object.class);
    private final Type ASM_TYPE_SPY = Type.getType(Spy.class);
    private final Type ASM_TYPE_THROWABLE = Type.getType(Throwable.class);
    private final CodeLock codeLockForTracing = new TracingAsmCodeLock(this);
    private boolean methodEnter = false;
    private int lineNumber;

    @Override
    protected void onMethodEnter() {
        if (eventSwitch.test(Event.Type.BEFORE)) {
            codeLockForTracing.lock(() -> {
                mark(beginLabel);
                loadArgArray();
                dup();
                push(adviceId);
                loadClassLoader();
                push(toJavaClassName(targetJavaClassName));
                push(getName());
                push(descriptor);
                loadThisOrPushNullIfIsStatic();
                invokeStatic(ASM_TYPE_SPY, AsmMethods.ON_BEFORE);
//                    swap();
                methodEnter = true;
                pop();
            });
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (eventSwitch.test(Event.Type.RETURN)) {
            codeLockForTracing.lock(() -> {
                if (!isThrow(opcode)) {
                    loadReturn(opcode);
                    dup();
                    push(adviceId);
                    invokeStatic(ASM_TYPE_SPY, AsmMethods.ON_RETURN);
                    pop();
                }
            });
        }
    }

    //
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (eventSwitch.test(Event.Type.THROWS)) {
            mark(endLabel);
            visitTryCatchBlock(beginLabel, endLabel, mark(),
                    ASM_TYPE_THROWABLE.getInternalName());

            codeLockForTracing.lock(() -> {
                // 加载Throwable
                dup();
                dup();
                push(adviceId);
                invokeStatic(ASM_TYPE_SPY, AsmMethods.ON_THROWS);
                pop();
            });
            throwException();
        }

        super.visitMaxs(maxStack, maxLocals);
    }

    /**
     * 加载返回值
     *
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
     *
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
            invokeStatic(ASM_TYPE_CLASS, AsmMethods.FOR_NAME);

        } else {
            loadThis();
            invokeVirtual(ASM_TYPE_OBJECT, AsmMethods.GET_CLASS);
        }
        invokeVirtual(ASM_TYPE_CLASS, AsmMethods.GET_CLASSLOADER);

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
     *
     * @param opcode 操作码
     * @return true:以抛异常形式返回 / false:非抛异常形式返回(return)
     */
    private boolean isThrow(int opcode) {
        return opcode == ATHROW;
    }

    protected boolean isSuperOrSiblingConstructorCall(int opcode, String owner, String name) {
        return (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")
                && (superName.equals(owner) || targetJavaClassName.equals(owner)));
    }

    @Override
    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        if (isSuperOrSiblingConstructorCall(opcodeAndSource, owner, name) || !methodEnter || codeLockForTracing.isLock()) {
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
            return;
        }

        if (eventSwitch.test(Event.Type.CALL_BEFORE)) {
            codeLockForTracing.lock(() -> {
                push(adviceId);
                push(toJavaClassName(owner));
                push(name);
                push(descriptor);
                push(lineNumber);
                invokeStatic(ASM_TYPE_SPY, AsmMethods.ON_CALL_BEFORE);
            });
        }

        final boolean enableThrows = eventSwitch.test(Event.Type.CALL_THROWS);
        if (!enableThrows) {
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
            traceCallReturn();
            return;
        }

        // 这里是需要处理拥有CALL_THROWS事件的场景
        final Label tracingBeginLabel = new Label();
        final Label tracingEndLabel = new Label();
        final Label tracingFinallyLabel = new Label();

        // try
        // {

        mark(tracingBeginLabel);
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
        mark(tracingEndLabel);

        traceCallReturn();

        goTo(tracingFinallyLabel);

        catchException(tracingBeginLabel, tracingEndLabel, ASM_TYPE_THROWABLE);
        codeLockForTracing.lock(() -> {
            dup();
            push(adviceId);
            push(lineNumber);
            invokeStatic(ASM_TYPE_SPY, AsmMethods.ON_CALL_THROWS);
        });

        throwException();
        mark(tracingFinallyLabel);
    }

    private void traceCallReturn() {
        if (eventSwitch.test(Event.Type.CALL_RETURN)) {
            codeLockForTracing.lock(() -> {
                push(adviceId);
                push(lineNumber);
                invokeStatic(ASM_TYPE_SPY, AsmMethods.ON_CALL_RETURN);
            });
        }
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
        codeLockForTracing.code(opcode);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        if (eventSwitch.test(Event.Type.LINE)) {
            codeLockForTracing.lock(() -> {
                push(adviceId);
                push(line);
                invokeStatic(ASM_TYPE_SPY, AsmMethods.ON_LINE);
            });
        }
        super.visitLineNumber(line, start);
        this.lineNumber = line;
    }
}
