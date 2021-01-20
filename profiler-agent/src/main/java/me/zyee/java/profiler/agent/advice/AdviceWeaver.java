package me.zyee.java.profiler.agent.advice;

import java.util.HashSet;
import java.util.Set;
import me.zyee.java.profiler.event.Event;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.JSRInlinerAdapter;

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
    private final Event.Type[] types;

    public AdviceWeaver(int adviceId, Set<String> signCodes, String targetJavaClassName, ClassVisitor cv, Event.Type[] types) {
        super(Opcodes.ASM7, cv);
        this.adviceId = adviceId;
        this.signCodes = new HashSet<>(signCodes);
        this.targetJavaClassName = targetJavaClassName;
        this.types = types;
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

        return new ProfilerAdviceAdapter(
                new JSRInlinerAdapter(mv, access, name, descriptor, signature, exceptions),
                access, name, descriptor, adviceId, targetJavaClassName, superName, type -> ArrayUtils.contains(types, type));
    }

}
