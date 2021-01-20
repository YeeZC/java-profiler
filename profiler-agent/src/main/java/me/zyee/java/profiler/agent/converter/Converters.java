package me.zyee.java.profiler.agent.converter;

import java.util.NoSuchElementException;
import me.zyee.java.profiler.agent.converter.string.ConstructorConverter;
import me.zyee.java.profiler.agent.converter.string.FromConverter;
import me.zyee.java.profiler.agent.converter.string.FromStringConverter;
import me.zyee.java.profiler.agent.converter.string.ValueOfConverter;
import org.apache.commons.lang3.ClassUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/20
 */
public class Converters {

    public static <T> FromStringConverter<T> create(Class<T> type) {
        if (type == String.class) {
            return type::cast;
        }
        final Class<T> clazz = (Class<T>) ClassUtils.primitiveToWrapper(type);
        FromStringConverter<T> converter = ValueOfConverter.getIfEligible(clazz);
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
