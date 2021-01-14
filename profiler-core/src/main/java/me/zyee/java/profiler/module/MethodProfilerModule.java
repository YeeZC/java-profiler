package me.zyee.java.profiler.module;

import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileHandler;
import me.zyee.java.profiler.ProfileHandlerRegistry;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.Profiler;
import me.zyee.java.profiler.annotation.Profile;
import me.zyee.java.profiler.event.Before;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.Throws;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.ProfileBehaviorFilter;
import me.zyee.java.profiler.impl.ContextHelper;
import org.apache.commons.lang3.reflect.MethodUtils;

import javax.annotation.Resource;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/12
 */
public class MethodProfilerModule implements Module {

    @Resource
    private EventWatcher watcher;

    private int watchId;
    private long start;
    private ProfileItem item;
    private Context context;
    private Profiler profiler;
    private final List<ActualCostCountModule> modules = new ArrayList<>();

    @Override
    public void enable() {
        this.watchId = watcher.watch(new ProfileBehaviorFilter(), event -> {
            switch (event.type()) {
                case BEFORE:
                    return onBefore((Before) event);
                case RETURN:
                    return onReturn();
                case THROWS:
                    return onThrows((Throws) event);
                default:
            }
            return false;
        }, Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS);
    }

    @Override
    public void disable() {
        watcher.delete(watchId);
    }

    private boolean onBefore(Before before) {
        context = ContextHelper.getContext()
                .resolve(before.getTriggerClass() + "#" + before.getTriggerMethod()
                        + System.currentTimeMillis());
        item = new ProfileItem(before.getTriggerMethod());
        profiler = context.getProfiler();
        if (null != profiler) {
            try {
                final Class<?> clazz = before.getTriggerLoader().loadClass(before.getTriggerClass());
                final MethodType methodType = MethodType.fromMethodDescriptorString(before.getTriggerMethodSign(), before.getTriggerLoader());
                final Method method = MethodUtils.getMatchingMethod(clazz, before.getTriggerMethod(), methodType.parameterArray());
                final Profile profile = method.getAnnotation(Profile.class);
                final String[] patterns = profile.strictCount();
                if (patterns.length > 0) {
                    Stream.of(patterns).distinct().map(ActualCostCountModule::new)
                            .map(CoreModule::enableModule)
                            .forEach(modules::add);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            profiler.start();
            start = System.currentTimeMillis();
        }
        return false;
    }

    private boolean onReturn() {
        if (null != profiler) {
            try {
                final ProfileHandler handler = ProfileHandlerRegistry.getHandler();
                item.offer(handler.next());
                item.setCost(System.currentTimeMillis() - start);
                final Path stop = profiler.stop();
                item.setFlamePath(stop);
                Optional.ofNullable(context.getProfileItems()).ifPresent(queue -> queue.offer(item));
                System.out.println("profile tree 输出路径 " + stop);
            } catch (NullPointerException e) {
                item.setThrowable(e);
            } finally {
                final Map<String, Supplier<Long>> costs = modules.stream().peek(Module::disable)
                        .collect(Collectors.toMap(ActualCostCountModule::getPattern,
                                ActualCostCountModule::getReference));
                item.setActualCost(costs);
            }
        }
        return false;
    }

    private boolean onThrows(Throws throwsEvent) {
        item.setThrowable(throwsEvent.getThrowable());
        return onReturn();
    }
}
