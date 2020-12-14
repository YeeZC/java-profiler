package me.zyee.java.profiler.agent;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.Callable;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.annotation.Atoms;
import me.zyee.java.profiler.impl.ContextHelper;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class AgentInterceptor {
    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @SuperCall Callable<Object> callable) throws Exception {
        Context context = ContextHelper.getContext().resolve(method.getName()
                + System.currentTimeMillis());
        final ProfileItem item = new ProfileItem(method.toGenericString());
        final Atoms annotation = method.getAnnotation(Atoms.class);
        item.setAtoms(annotation);
        try {
            final Object result = Optional.ofNullable(context.getProfiler()).map(profiler -> {
                profiler.start();
                long start = System.currentTimeMillis();
                try {
                    // 原有函数执行
                    return callable.call();
                } catch (Exception e) {
                    item.setThrowable(e);
                    return null;
                } finally {
                    item.setCost(System.currentTimeMillis() - start);
                    item.setFlamePath(profiler.stop());
                    Optional.ofNullable(context.getProfileItems()).ifPresent(queue -> queue.offer(item));
                }
            }).orElseGet(() -> {
                try {
                    return callable.call();
                } catch (Exception e) {
                    item.setThrowable(e);
                    return null;
                }
            });
            Throwable throwable = item.getThrowable();
            if (throwable instanceof Exception) {
                throw (Exception) throwable;
            } else if (throwable != null) {
                throw new Exception(throwable);
            }
            return result;
        } catch (Exception e) {
            return callable.call();
        }
    }
}
