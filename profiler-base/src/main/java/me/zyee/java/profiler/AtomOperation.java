package me.zyee.java.profiler;

import java.util.function.Supplier;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public interface AtomOperation extends Operation {
    long getWhen();

    long getExpect();

    Supplier<Long> getActual();
}
