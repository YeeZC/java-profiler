package me.zyee.java.profiler.agent.converter.string;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/20
 */
public abstract class BaseStringConverter<T> implements FromStringConverter<T> {
    protected final Class<T> clazz;

    public BaseStringConverter(Class<T> clazz) {
        this.clazz = clazz;
    }
}
