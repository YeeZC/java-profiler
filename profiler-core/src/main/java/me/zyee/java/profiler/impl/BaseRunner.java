package me.zyee.java.profiler.impl;

import java.nio.file.Path;
import java.util.Optional;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileHandler;
import me.zyee.java.profiler.ProfileHandlerRegistry;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.Result;
import me.zyee.java.profiler.Runner;
import me.zyee.java.profiler.Task;
import me.zyee.java.profiler.annotation.Profile;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public abstract class BaseRunner implements Runner, Task {

    protected final Class<?> targetClass;

    public BaseRunner(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public Result apply(Context context) {
        try {
            if (targetClass.isAnnotationPresent(Profile.class)) {
                try {
                    return Optional.ofNullable(context.getProfiler()).map(profiler -> {
                        final ProfileItem item = new ProfileItem(targetClass.getName());
                        final ProfileHandler handler = ProfileHandlerRegistry.getHandler();
                        item.offer(handler.next());
                        profiler.start();
                        long start = System.currentTimeMillis();
                        try {
                            return run();
                        } catch (Exception e) {
                            item.setThrowable(e);
                            return Result.failed(e);
                        } finally {
                            item.setCost(System.currentTimeMillis() - start);
                            final Path stop = profiler.stop();
                            item.setFlamePath(stop);
                            Optional.ofNullable(context.getProfileItems()).ifPresent(queue -> queue.offer(item));
                        }
                    }).orElseGet(() -> Result.failed(new Exception("Flame Profiler not found")));
                } catch (Exception e) {
                    return Result.failed(e);
                }
            }
            return run();
        } catch (Exception e) {
            return Result.failed(e);
        } finally {
            ProfileHandlerRegistry.remove();
            ContextHelper.removeContext(context);
        }
    }

    @Override
    public String name() {
        return targetClass.getName();
    }
}
