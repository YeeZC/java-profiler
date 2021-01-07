package me.zyee.profiler.agent.core.utils;

import me.zyee.profiler.spy.Spy;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.objectweb.asm.commons.Method;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2020/4/23
 */
public class AsmMethods {
    public static Method ON_BEFORE = getAsmMethod(Spy.class, "onBefore",
            int.class,
            ClassLoader.class,
            String.class,
            String.class,
            String.class,
            Object.class,
            Object[].class);
    public static Method ON_RETURN = getAsmMethod(Spy.class, "onReturn",
            int.class,
            Object.class);
    public static Method ON_THROWS = getAsmMethod(Spy.class, "onThrows",
            int.class,
            Throwable.class);

    public static Method getAsmMethod(final Class<?> clazz,
                                      final String methodName,
                                      final Class<?>... parameterClassArray) {
        return Method.getMethod(MethodUtils.getMatchingMethod(clazz, methodName, parameterClassArray));
    }


}
