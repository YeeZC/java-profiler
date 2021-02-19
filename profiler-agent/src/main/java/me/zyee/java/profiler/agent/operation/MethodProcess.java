package me.zyee.java.profiler.agent.operation;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/29
 */
public interface MethodProcess {
    Object process(Object result, Object... args) throws Throwable;
}
