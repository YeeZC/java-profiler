package me.zyee.java.profiler.agent.operation;

import java.lang.reflect.Method;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/29
 */
public abstract class BaseMethodProcess implements MethodProcess {
    protected final Method method;

    public BaseMethodProcess(Method method) {
        this.method = method;
    }
}
