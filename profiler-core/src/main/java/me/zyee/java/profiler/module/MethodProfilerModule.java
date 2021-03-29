package me.zyee.java.profiler.module;

import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileHandler;
import me.zyee.java.profiler.ProfileHandlerRegistry;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.Profiler;
import me.zyee.java.profiler.annotation.PostProcessor;
import me.zyee.java.profiler.annotation.Profile;
import me.zyee.java.profiler.event.Before;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.Return;
import me.zyee.java.profiler.event.Throws;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.BehaviorFilter;
import me.zyee.java.profiler.filter.ProfileBehaviorFilter;
import me.zyee.java.profiler.impl.ContextHelper;
import me.zyee.java.profiler.utils.FormatUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import javax.annotation.Resource;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    protected ProfileItem item;
    private Profiler profiler;
    private final List<ActualCostCountModule> modules = new ArrayList<>();
    private final List<PostProcessor> processors = new ArrayList<>();

    @Override
    public void enable() {
        this.watchId = watcher.watch(getFilter(), event -> {
            switch (event.type()) {
                case BEFORE:
                    return onBefore((Before) event);
                case RETURN:
                    for (PostProcessor processor : this.processors) {
                        processor.after(((Return) event).getReturnObject());
                    }
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

    @Override
    public EventWatcher getWatcher() {
        return watcher;
    }

    protected boolean onBefore(Before before) {
        final Context parent = ContextHelper.getContext();
        final String name = transferProfileName(parent.name(), before);
        Context context = parent.resolve(name);
        item = new ProfileItem(name);
        Optional.ofNullable(context.getProfileItems()).ifPresent(queue -> queue.offer(item));
        profiler = context.getProfiler();
        if (null != profiler) {
            try {
                final Class<?> clazz = before.getTriggerLoader().loadClass(before.getTriggerClass());
                final MethodType methodType = MethodType.fromMethodDescriptorString(before.getTriggerMethodSign(), before.getTriggerLoader());
                final Method method = MethodUtils.getMatchingMethod(clazz, before.getTriggerMethod(), methodType.parameterArray());
                final Profile profile = method.getAnnotation(Profile.class);
                if (null != profile) {
                    final String[] patterns = profile.counters();
                    if (patterns.length > 0) {
                        Stream.of(patterns).distinct().map(ActualCostCountModule::new)
                                .peek(Module::enable)
                                .forEach(modules::add);
                    }
                    final Class<? extends PostProcessor>[] processors = profile.processors();
                    for (Class<? extends PostProcessor> processor : processors) {

                        try {
                            final PostProcessor p = processor.newInstance();
                            p.before(before.getArgs());
                            this.processors.add(p);
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                getActualCountPatterns().stream().map(ActualCostCountModule::new)
                        .peek(Module::enable)
                        .forEach(modules::add);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            profiler.start();
            start = System.currentTimeMillis();
        }
        return false;
    }

    protected boolean onReturn() {
        if (null != profiler) {
            try {
                final ProfileHandler handler = ProfileHandlerRegistry.getHandler();
                item.offer(handler.next());
                item.setCost(System.currentTimeMillis() - start);
                final Path stop = profiler.stop();
                item.setFlamePath(stop);
                System.out.println("profile tree 输出路径 " + stop);
            } catch (NullPointerException e) {
                item.setThrowable(e);
            } finally {
                final Map<String, Supplier<Long>> costs = modules.stream().peek(Module::disable)
                        .collect(Collectors.toMap(ActualCostCountModule::getPattern,
                                ActualCostCountModule::getReference));
                final Map<String, Supplier<Long>> collect =
                        ContextHelper.COUNTER.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> entry.getValue()::get));
                ContextHelper.COUNTER.clear();
                collect.forEach((key, value) -> {
                    costs.merge(key, value, (s, s1) -> () -> s.get() + s1.get());
                });
                item.setActualCost(costs);
            }
        }
        return false;
    }

    protected boolean onThrows(Throws throwsEvent) {
        item.setThrowable(throwsEvent.getThrowable());
        return onReturn();
    }

    protected String transferProfileName(String name, Before before) {
        return (StringUtils.isEmpty(name) ? before.getTriggerClass() : name).replace("/", ".") + "#" + before.getTriggerMethod()
                + FormatUtil.formatNow();
    }

    protected BehaviorFilter getFilter() {
        return new ProfileBehaviorFilter();
    }

    protected Set<String> getActualCountPatterns() {
        return new HashSet<>();
    }
}
