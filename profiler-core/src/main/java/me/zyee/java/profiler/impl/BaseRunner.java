package me.zyee.java.profiler.impl;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileHandler;
import me.zyee.java.profiler.ProfileHandlerRegistry;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.Result;
import me.zyee.java.profiler.Runner;
import me.zyee.java.profiler.Task;
import me.zyee.java.profiler.annotation.Profile;
import me.zyee.java.profiler.module.ActualCostCountModule;
import me.zyee.java.profiler.module.CoreModule;
import me.zyee.java.profiler.module.Module;

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
            if (!CoreModule.isWarmup() && targetClass.isAnnotationPresent(Profile.class)) {
                final Profile profile = targetClass.getAnnotation(Profile.class);
                final String[] strings = profile.strictCount();
                final List<ActualCostCountModule> collect = Stream.of(strings).distinct().map(ActualCostCountModule::new)
                        .map(CoreModule::enableModule)
                        .collect(Collectors.toList());
                try {
                    return Optional.ofNullable(context.getProfiler()).map(profiler -> {
                        final ProfileItem item = new ProfileItem(targetClass.getName());
                        profiler.start();
                        long start = System.currentTimeMillis();
                        try {
                            return run();
                        } catch (Exception e) {
                            item.setThrowable(e);
                            return Result.failed(e);
                        } finally {
                            final ProfileHandler handler = ProfileHandlerRegistry.getHandler();
                            item.offer(handler.next());
                            item.setCost(System.currentTimeMillis() - start);
                            final Map<String, Supplier<Long>> costs = collect.stream()
                                    .peek(Module::disable)
                                    .collect(Collectors.toMap(ActualCostCountModule::getPattern,
                                            ActualCostCountModule::getReference));
                            item.setActualCost(costs);
                            final Path stop = profiler.stop();
                            System.out.println("profile tree 输出路径 " + stop);
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
