package me.zyee.java.profiler.converter;

import java.util.function.Function;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/20
 */
public interface Converter<P, R> extends Function<P, R> {
    R convert(P input);

    @Override
    default R apply(P p) {
        return convert(p);
    }
}
