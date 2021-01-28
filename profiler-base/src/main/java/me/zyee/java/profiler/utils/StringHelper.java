package me.zyee.java.profiler.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
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

    public static <T> T fromArgs(String arg, T configure) {
        if (null == arg) {
            return configure;
        }
        final byte[] bytes = arg.replace(";", "\n").getBytes(StandardCharsets.UTF_8);
        Properties properties = new Properties();
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            properties.load(is);
        } catch (IOException e) {
            return configure;
        }
        final Field[] fields = FieldUtils.getAllFields(configure.getClass());
        for (Field field : fields) {
            try {
                final String property = properties.getProperty(field.getName());
                if (StringUtils.isNotEmpty(property)) {
                    final String2ObjectConverter<?> converter = String2Objects.create(field.getType(), field);
                    FieldUtils.writeField(field, configure, converter.convert(property), true);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
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
                                    joiner.add(field.getName() + "=" + ((Enum<?>)value).name());
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
