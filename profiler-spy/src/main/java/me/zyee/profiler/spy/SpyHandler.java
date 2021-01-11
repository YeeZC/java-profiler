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

    void onCallBefore(int listenId, String className, String methodName, String desc, int lineNumber) throws Throwable;

    void onCallReturn(int listenId, int lineNumber) throws Throwable;

    void onCallThrows(int listenId, int lineNumber, Throwable throwMsg) throws Throwable;

    void onLine(int listenId, int lineNumber) throws Throwable;
}
