package me.zyee.java.profiler.converter.obj;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.zyee.java.profiler.utils.StringHelper;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/28
 */
public class String2ListConverter<T> implements String2ObjectConverter<List<T>> {
    private final Class<T> component;

    private String2ListConverter(Class<T> component) {
        this.component = component;
    }

    @Override
    public List<T> convert(String input) {
        final String[] split = input.replace(":", ";").split(",");
        List<T> result = new ArrayList<>();
        for (String item : split) {
            try {
                final T t = StringHelper.fromArgs(item, component.newInstance());
                result.add(t);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static <T> String2ObjectConverter<T> getIfEligible(Class<T> clazz, Type genericType) {
        if (clazz.isArray()) {
            return input -> (T) new String2ListConverter<>(clazz.getComponentType())
                    .convert(input).toArray((Object[]) Array.newInstance(clazz.getComponentType(), 0));
        }
        if (genericType instanceof ParameterizedType) {
            final Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            if (Set.class.isAssignableFrom(clazz)) {
                return input -> (T) new HashSet<>(new String2ListConverter<>((Class<?>) type).convert(input));
            }
            if (Collection.class.isAssignableFrom(clazz)) {
                return new String2ListConverter((Class<?>) type);
            }
        }

        return null;
    }
}
