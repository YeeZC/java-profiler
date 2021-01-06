package me.zyee.profiler.spy;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public interface SpyHandler {
    void onBefore(int listenId, ClassLoader loader, String className, String methodName, String methodDesc,
                  Object target, Object[] args) throws Throwable;

    void onReturn(int listenId, Object returnObject) throws Throwable;

    void onThrows(int listenId, Throwable throwable) throws Throwable;
}
