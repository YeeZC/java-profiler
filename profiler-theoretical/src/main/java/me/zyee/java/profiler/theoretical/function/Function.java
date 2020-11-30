package me.zyee.java.profiler.theoretical.function;

import me.zyee.java.profiler.theoretical.formula.Formula;

/**
 * 自定义方法
 *
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public interface Function<T> extends Formula<T> {
    int getParameterCount();

    String getName();
}
