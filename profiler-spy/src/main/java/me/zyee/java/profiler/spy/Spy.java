package me.zyee.java.profiler.spy;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class Spy {
    public static final int START = 1000;
    private static final AtomicInteger SEQUENCE_REF = new AtomicInteger(START);

    public static int nextSequence() {
        return SEQUENCE_REF.getAndIncrement();
    }

    private static SpyHandler handler = null;

    public static void init(SpyHandler handler) {
        Spy.handler = handler;
    }

    public static void destroy() {
        handler = null;
    }

    public static void onEntry(int listenId) throws Throwable {
        if (null != handler) {
            handler.onEntry(listenId);
        }
    }

    public static void onBefore(Object[] args,
                                int listenId,
                                ClassLoader loader,
                                String className,
                                String methodName,
                                String methodDesc,
                                Object target
    ) throws Throwable {
        if (null != handler) {
            handler.onBefore(listenId, loader, className, methodName, methodDesc, target, args);
        }
    }

    public static void onReturn(Object object, int listenId) throws Throwable {
        if (null != handler) {
            handler.onReturn(listenId, object);
        }
    }

    public static void onThrows(Throwable object, int listenId) throws Throwable {
        if (null != handler) {
            handler.onThrows(listenId, object);
        }
    }

    public static void onCallBefore(int listenId, String className, String methodName, String methodDesc, int lineNumber) throws Throwable {
        if (null != handler) {
            handler.onCallBefore(listenId, className, methodName, methodDesc, lineNumber);
        }
    }

    public static void onCallReturn(int listenId, int lineNumber) throws Throwable {
        if (null != handler) {
            handler.onCallReturn(listenId, lineNumber);
        }
    }

    public static void onCallThrows(Throwable throwable, int listenId, int lineNumber) throws Throwable {
        if (null != handler) {
            handler.onCallThrows(listenId, lineNumber, throwable);
        }
    }

    public static void onLine(int listenId, int lineNumber) throws Throwable {
        if (null != handler) {
            handler.onLine(listenId, lineNumber);
        }
    }
}
