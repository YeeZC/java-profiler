package me.zyee.java.profiler.converter.obj;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/20
 */
public abstract class BaseString2ObjectConverter<T> implements String2ObjectConverter<T> {
    protected final Class<T> clazz;

    public BaseString2ObjectConverter(Class<T> clazz) {
        this.clazz = clazz;
    }
}
