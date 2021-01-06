package me.zyee.profiler.spy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class Spy {
    private static final AtomicInteger sequenceRef = new AtomicInteger(1000);

    public static int nextSequence() {
        return sequenceRef.getAndIncrement();
    }

    private static final Map<String, SpyHandler> HANDLERS = new ConcurrentHashMap<>();

    public static boolean isInit(String namespace) {
        return HANDLERS.containsKey(namespace);
    }

    public static void init(String namespace, SpyHandler handler) {
        HANDLERS.putIfAbsent(namespace, handler);
    }

    public static void destroy(String namespace) {
        HANDLERS.remove(namespace);
    }

    public static void onBefore(String namespace, int listenId, ClassLoader loader, String className, String methodName, String methodDesc,
                                Object target, Object[] args) throws Throwable {
        final SpyHandler spyHandler = HANDLERS.get(namespace);
        if (null != spyHandler) {
            spyHandler.onBefore(listenId, loader, className, methodName, methodDesc, target, args);
        }
    }

    public static void onReturn(String namespace, int listenId, Object object) throws Throwable {
        final SpyHandler spyHandler = HANDLERS.get(namespace);
        if (null != spyHandler) {
            spyHandler.onReturn(listenId, object);
        }
    }

    public static void onThrows(String namespace, int listenId, Throwable object) throws Throwable {
        final SpyHandler spyHandler = HANDLERS.get(namespace);
        if (null != spyHandler) {
            spyHandler.onThrows(listenId, object);
        }
    }
}
