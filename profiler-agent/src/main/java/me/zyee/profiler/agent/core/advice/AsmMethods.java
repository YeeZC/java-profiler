package me.zyee.profiler.agent.core.advice;

import me.zyee.profiler.spy.Spy;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.objectweb.asm.commons.Method;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/11
 */
public class AsmMethods {
    public static final Method ON_BEFORE = getAsmMethod(Spy.class, "onBefore",
            Object[].class,
            int.class,
            ClassLoader.class,
            String.class,
            String.class,
            String.class,
            Object.class
    );
    public static final Method ON_RETURN = getAsmMethod(Spy.class, "onReturn",
            Object.class,
            int.class);
    public static final Method ON_THROWS = getAsmMethod(Spy.class, "onThrows",
            Throwable.class,
            int.class
    );
    public static final Method ON_LINE = getAsmMethod(Spy.class, "onLine",
            int.class,
            int.class);
    public static final Method ON_CALL_BEFORE = getAsmMethod(Spy.class, "onCallBefore",
            int.class,
            String.class,
            String.class,
            String.class,
            int.class);
    public static final Method ON_CALL_RETURN = getAsmMethod(Spy.class, "onCallReturn",
            int.class,
            int.class);
    public static final Method ON_CALL_THROWS = getAsmMethod(Spy.class, "onCallThrows",
            Throwable.class,
            int.class,
            int.class
    );

    public static final Method FOR_NAME = getAsmMethod(Class.class, "forName", String.class);
    public static final Method GET_CLASS = getAsmMethod(Class.class, "getClass");
    public static final Method GET_CLASSLOADER = getAsmMethod(Class.class, "getClassLoader");

    public static Method getAsmMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return Method.getMethod(MethodUtils.getMatchingMethod(clazz, name, parameterTypes));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
