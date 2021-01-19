package me.zyee.profiler.agent.core.enhancer;

import java.lang.reflect.Array;
import java.util.Set;
import me.zyee.java.profiler.event.Event;
import me.zyee.profiler.agent.loader.ProfilerClassLoader;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/19
 */
public class EnhancerProxy implements Enhancer {
    private final Object delegate;
    private final ClassLoader profilerLoader;

    public EnhancerProxy() {
        this.profilerLoader = ProfilerClassLoader.getInstance();
        Object enhancer;
        try {
            final Class<?> clazz = this.profilerLoader.loadClass("me.zyee.profiler.agent.core.enhancer.EventEnhancer");
            enhancer = clazz.newInstance();
        } catch (Throwable e) {
            enhancer = new EventEnhancer();
        }
        this.delegate = enhancer;

    }

    @Override
    public byte[] toByteCodeArray(ClassLoader loader,
                                  byte[] srcByteCodeArray,
                                  Set<String> signCodes,
                                  int listenerId,
                                  Event.Type[] eventTypeArray) {
        try {
            final Class typeClass = profilerLoader.loadClass("me.zyee.java.profiler.event.Event$Type");
            final Object array = Array.newInstance(typeClass, eventTypeArray.length);
            for (int i = 0; i < eventTypeArray.length; i++) {
                Array.set(array, i, Enum.valueOf(typeClass, eventTypeArray[i].name()));
            }
            return (byte[]) MethodUtils.invokeMethod(delegate,
                    "toByteCodeArray",
                    loader, srcByteCodeArray, signCodes, listenerId, array);
        } catch (Throwable e) {
            return ((Enhancer) delegate).toByteCodeArray(loader, srcByteCodeArray, signCodes, listenerId, eventTypeArray);
        }
    }

    private static final class SingletonHolder {
        private static final Enhancer INSTANCE = new EnhancerProxy();
    }

    public static Enhancer getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
