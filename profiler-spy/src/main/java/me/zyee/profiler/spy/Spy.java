package me.zyee.profiler.spy;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class Spy {

    public static Method ON_BEFORE = getMethod(Spy.class, "onBefore",
            int.class,
            ClassLoader.class,
            String.class,
            String.class,
            String.class,
            Object.class,
            Object[].class);
    public static Method ON_RETURN = getMethod(Spy.class, "onReturn",
            int.class,
            Object.class);
    public static Method ON_THROWS = getMethod(Spy.class, "onThrows",
            int.class,
            Throwable.class);

    private static Method getMethod(Class<?> clazz, String name, Class<?>... params) {
        return MethodUtils.getMatchingMethod(clazz, name, params);
    }

    private static final AtomicInteger sequenceRef = new AtomicInteger(1000);

    public static int nextSequence() {
        return sequenceRef.getAndIncrement();
    }

    private static SpyHandler handler = null;

    public static void init(SpyHandler handler) {
        Spy.handler = handler;
    }

    public static void destroy() {
        handler = null;
    }

    public static void onBefore(int listenId,
                                ClassLoader loader,
                                String className,
                                String methodName,
                                String methodDesc,
                                Object target,
                                Object[] args) throws Throwable {
        if (null != handler) {
            handler.onBefore(listenId, loader, className, methodName, methodDesc, target, args);
        }
    }

    public static void onReturn(int listenId, Object object) throws Throwable {
        if (null != handler) {
            handler.onReturn(listenId, object);
        }
    }

    public static void onThrows(int listenId, Throwable object) throws Throwable {
        if (null != handler) {
            handler.onThrows(listenId, object);
        }
    }
}
