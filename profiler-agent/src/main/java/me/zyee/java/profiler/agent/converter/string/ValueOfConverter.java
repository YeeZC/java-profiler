package me.zyee.java.profiler.agent.converter.string;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/20
 */
public class ValueOfConverter<T> extends BaseStringConverter<T> {
    private static final String VALUE_OF = "valueOf";
    private final Method method;

    public ValueOfConverter(Class<T> clazz, Method method) {
        super(clazz);
        this.method = method;
    }

    @Override
    public T convert(String input) {
        try {
            return clazz.cast(method.invoke(null, input));
        } catch (Throwable e) {
            if (e.getCause() != null) {
                throw new IllegalArgumentException(e.getCause());
            } else {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static <T> ValueOfConverter<T> getIfEligible(Class<T> clazz) {
        final Method method = MethodUtils.getMatchingMethod(clazz, VALUE_OF, String.class);
        if (Modifier.isStatic(method.getModifiers())) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return new ValueOfConverter<>(clazz, method);
        } else {
            // The valueOf method is present but it must be static.
            return null;
        }
    }
}
