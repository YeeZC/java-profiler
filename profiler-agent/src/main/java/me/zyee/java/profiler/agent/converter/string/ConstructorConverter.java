package me.zyee.java.profiler.agent.converter.string;

import java.lang.reflect.Constructor;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/20
 */
public class ConstructorConverter<T> extends BaseStringConverter<T> {
    private final Constructor<T> constructor;

    public ConstructorConverter(Class<T> clazz, Constructor<T> constructor) {
        super(clazz);
        this.constructor = constructor;
    }

    @Override
    public T convert(String input) {
        try {
            return constructor.newInstance(input);
        } catch (Throwable e) {
            if (e.getCause() != null) {
                throw new IllegalArgumentException(e.getCause());
            } else {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static <T> ConstructorConverter<T> getIfEligible(Class<T> clazz) {
        try {
            final Constructor<T> constructor = clazz.getConstructor(String.class);
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return new ConstructorConverter<T>(clazz, constructor);
        } catch (NoSuchMethodException e) {
            // The class does not have the right constructor, return null.
            return null;
        }
    }
}
