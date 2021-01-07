package me.zyee.profiler.agent.event.watcher;

import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.BehaviorFilter;
import me.zyee.java.profiler.utils.GroupMatcher;
import me.zyee.java.profiler.utils.Matcher;
import me.zyee.java.profiler.utils.SearchUtils;
import me.zyee.profiler.agent.core.transformer.ProfilerTransformer;
import me.zyee.profiler.agent.event.handler.EventHandler;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/6
 */
public class DefaultEventWatcher implements EventWatcher {
    private final Instrumentation inst;
    private final EventHandler handler;
    private final List<ProfilerTransformer> transformers = new ArrayList<>();

    public DefaultEventWatcher(Instrumentation inst, EventHandler handler) {
        this.inst = inst;
        this.handler = handler;
    }

    @Override
    public int watch(BehaviorFilter filter, EventListener listener, Event.Type... types) {
        final ProfilerTransformer transformer = new ProfilerTransformer(filter, listener, types);
        transformers.add(transformer);
        Set<Class<?>> classes = SearchUtils.searchClass(() -> Stream.of(inst.getAllLoadedClasses()),
                className -> transformer.getFilter().classFilter(className));
        final int id = transformer.getId();
//        if (!classes.isEmpty()) {
        inst.addTransformer(transformer, true);
        handler.register(id, listener, types);
        try {
            if (!classes.isEmpty()) {
                inst.retransformClasses(classes.toArray(new Class[0]));
            }
        } catch (UnmodifiableClassException ignore) {
            ignore.printStackTrace();
        }
//        }
        return id;
    }

    @Override
    public int watch(BehaviorFilter pattern, EventListener listener) {
        return watch(pattern, listener, Event.Type.values());
    }

    @Override
    public void delete(int id) {
        final Iterator<ProfilerTransformer> it = transformers.iterator();
        Set<BehaviorFilter> matchers = new HashSet<>();
        while (it.hasNext()) {
            final ProfilerTransformer next = it.next();
            if (id == next.getId()) {
                handler.unRegister(next.getId());
                inst.removeTransformer(next);
                it.remove();
                matchers.add(next.getFilter());
            }
        }

        if (!matchers.isEmpty()) {
            Set<Class<?>> classes = SearchUtils.searchClass(() -> Arrays.stream(inst.getAllLoadedClasses()),
                    new GroupMatcher.Or<>(matchers.stream().map(filter -> (Matcher<String>) className -> filter.classFilter(className))
                            .collect(Collectors.toSet())));
            try {
                inst.retransformClasses(classes.toArray(new Class[0]));
            } catch (UnmodifiableClassException e) {
                e.printStackTrace();
            }
        }
    }
}
