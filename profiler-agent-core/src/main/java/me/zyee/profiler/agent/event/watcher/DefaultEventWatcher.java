package me.zyee.profiler.agent.event.watcher;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import me.zyee.java.profiler.utils.GroupMatcher;
import me.zyee.java.profiler.utils.Matcher;
import me.zyee.java.profiler.utils.SearchUtils;
import me.zyee.profiler.agent.core.transformer.ProfilerTransformer;
import me.zyee.profiler.agent.event.Event;
import me.zyee.profiler.agent.event.handler.EventHandler;
import me.zyee.profiler.agent.event.listener.EventListener;

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
    public int watch(String pattern, EventListener listener, Event.Type... types) {
        final ProfilerTransformer transformer = new ProfilerTransformer(pattern, listener, types);
        transformers.add(transformer);
        Set<Class<?>> classes = SearchUtils.searchClass(() -> Stream.of(inst.getAllLoadedClasses()),
                transformer.getClassMatcher());
        final int id = transformer.getId();
        if (!classes.isEmpty()) {
            inst.addTransformer(transformer, true);
            handler.register(id, listener, types);
            try {
                inst.retransformClasses(classes.toArray(new Class[0]));
            } catch (UnmodifiableClassException ignore) {
                ignore.printStackTrace();
            }
        }
        return id;
    }

    @Override
    public void delete(int id) {
        final Iterator<ProfilerTransformer> it = transformers.iterator();
        Set<Matcher<String>> matchers = new HashSet<>();
        while (it.hasNext()) {
            final ProfilerTransformer next = it.next();
            if (id == next.getId()) {
                handler.unRegister(next.getId());
                inst.removeTransformer(next);
                it.remove();
                matchers.add(next.getClassMatcher());
            }
        }
        if (!matchers.isEmpty()) {
            Set<Class<?>> classes = SearchUtils.searchClass(() -> Stream.of(inst.getAllLoadedClasses()), new GroupMatcher.Or<>(matchers));
            try {
                inst.retransformClasses(classes.toArray(new Class[0]));
            } catch (UnmodifiableClassException e) {
                e.printStackTrace();
            }
        }
    }
}
