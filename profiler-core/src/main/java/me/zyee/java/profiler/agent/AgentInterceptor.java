package me.zyee.java.profiler.agent;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.Callable;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileHandler;
import me.zyee.java.profiler.ProfileHandlerRegistry;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.Profiler;
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
        try {
            final Profiler profiler = context.getProfiler();
            Object result = null;
            if (null != profiler) {
                profiler.start();
                long start = System.currentTimeMillis();
                try {
                    // 原有函数执行
                    result = callable.call();
                } catch (Exception e) {
                    item.setThrowable(e);
                } finally {
                    final ProfileHandler handler = ProfileHandlerRegistry.getHandler();
                    item.offer(handler.next());
                    item.setCost(System.currentTimeMillis() - start);
                    item.setFlamePath(profiler.stop());
                    Optional.ofNullable(context.getProfileItems()).ifPresent(queue -> queue.offer(item));
                }
            } else {
                try {
                    result = callable.call();
                } catch (Exception e) {
                    item.setThrowable(e);
                }
            }
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
