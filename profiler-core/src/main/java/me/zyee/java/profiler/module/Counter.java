package me.zyee.java.profiler.module;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/3/29
 */
public interface Counter {
    void increment();

    long get();
}
