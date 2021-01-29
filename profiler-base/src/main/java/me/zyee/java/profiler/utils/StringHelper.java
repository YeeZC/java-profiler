package me.zyee.java.profiler.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;
import java.util.StringJoiner;
import me.zyee.java.profiler.converter.obj.String2ObjectConverter;
import me.zyee.java.profiler.converter.obj.String2Objects;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/28
 */
public class StringHelper {

    public static <T> T fromArgs(String args, T configure) {
        if (null == args) {
            return configure;
        }
        final String[] params = args.split(";");
        for (String param : params) {
            final String key = StringUtils.substringBefore(param, "=");
            final String value = StringUtils.substringAfter(param, "=");
            Class<?> clazz = configure.getClass();
            if (StringUtils.isNotEmpty(value)) {
                try {
                    Field field = FieldUtils.getDeclaredField(clazz, key, true);
                    while (null == field && clazz != Object.class) {
                        clazz = clazz.getSuperclass();
                        field = FieldUtils.getDeclaredField(clazz, key, true);
                    }
                    if (null == field) {
                        continue;
                    }
                    final String2ObjectConverter<?> converter = String2Objects.create(field.getType(), field);
                    FieldUtils.writeField(field, configure, converter.convert(value), true);
                } catch (IllegalAccessException ignore) {
                }
            }
        }
        return configure;
    }

    public static String toString(Object obj) {
        return Optional.ofNullable(obj).map(o -> {
            StringJoiner joiner = new StringJoiner(";");
            final Field[] fields = FieldUtils.getAllFields(o.getClass());
            for (Field field : fields) {
                try {
                    Optional.ofNullable(FieldUtils.readField(field, o, true))
                            .ifPresent(value -> {
                                final Class<?> type = field.getType();
                                if (ClassUtils.isPrimitiveOrWrapper(type) || type == String.class) {
                                    joiner.add(field.getName() + "=" + value);
                                } else if (type.isEnum()) {
                                    joiner.add(field.getName() + "=" + ((Enum<?>) value).name());
                                } else if (type.isArray()) {
                                    final int length = Array.getLength(value);
                                    StringJoiner items = new StringJoiner(",");
                                    for (int i = 0; i < length; i++) {
                                        items.add(toString(Array.get(value, i)));
                                    }
                                    joiner.add(field.getName() + "=" + items.toString().replace(";", ":"));
                                } else if (Collection.class.isAssignableFrom(type)) {
                                    Collection<?> collection = (Collection<?>) value;
                                    StringJoiner items = new StringJoiner(",");
                                    collection.stream().map(StringHelper::toString)
                                            .forEach(items::add);
                                    joiner.add(field.getName() + "=" + items.toString().replace(";", ":"));
                                } else {
                                    joiner.add(field.getName() + "=" + toString(value));
                                }

                            });

                } catch (Throwable ignore) {
                }
            }
            return joiner.toString();
        }).orElse(StringUtils.EMPTY);
    }

}
