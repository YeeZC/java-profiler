package me.zyee.java.profiler.agent;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class StrictInterceptor {
    public static Map<String, AtomicInteger> counter = new ConcurrentHashMap<>();

    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @SuperCall Callable<Object> callable) throws Exception {
        counter.compute(method.getDeclaringClass().getName() + "#" + method.getName(), (key, value) -> {
            if (null != value) {
                value.incrementAndGet();
            } else {
                value = new AtomicInteger(1);
            }
            return value;
        });
        return callable.call();
    }
}
