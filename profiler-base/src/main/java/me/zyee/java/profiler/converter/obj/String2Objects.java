package me.zyee.java.profiler.converter.obj;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.ClassUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/20
 */
public class String2Objects {

    public static <T> String2ObjectConverter<T> create(Class<T> type, Field field) {
        if (type == String.class) {
            return type::cast;
        }
        if (type.isEnum()) {
            return input -> (T) Enum.valueOf((Class) type, input);
        }
        String2ObjectConverter<T> converter = null;
        final Class<T> clazz = (Class<T>) ClassUtils.primitiveToWrapper(type);
        if (null != field && (clazz.isArray() || Collection.class.isAssignableFrom(clazz))) {
            converter = String2ListConverter.getIfEligible(clazz, field.getGenericType());
        }

        if (null == converter && field != null) {
            converter = ValueOfConverter.getIfEligible(clazz);
        }
        if (null == converter) {
            converter = FromConverter.getIfEligible(type);
        }
        if (null == converter) {
            converter = ConstructorConverter.getIfEligible(type);
        }
        if (null != converter) {
            return converter;
        }
        if (clazz == Character.class) {
            return input -> {
                if (input == null) {
                    throw new NullPointerException("input must not be null");
                }

                if (input.length() != 1) {
                    throw new IllegalArgumentException("The input string \"" + input + "\" cannot be converted to a " +
                            "character. The input's length must be 1");
                }

                return type.cast(input.charAt(0));
            };
        }
        throw new NoSuchElementException("Cannot find a converter able to create instance of " + type.getName());
    }
}
