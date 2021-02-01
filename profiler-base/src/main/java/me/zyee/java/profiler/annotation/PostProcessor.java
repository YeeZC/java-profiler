package me.zyee.java.profiler.annotation;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/2/1
 */
public interface PostProcessor {
    void before(Object... args);

    void after(Object value);
}
