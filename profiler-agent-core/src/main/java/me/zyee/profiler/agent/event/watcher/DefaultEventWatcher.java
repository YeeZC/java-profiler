package me.zyee.profiler.agent.event.watcher;

import me.zyee.profiler.agent.event.Event;
import me.zyee.profiler.agent.event.handler.EventHandler;
import me.zyee.profiler.agent.event.listener.EventListener;
import me.zyee.profiler.agent.transformer.ProfilerTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        inst.addTransformer(transformer, true);
        final int id = transformer.getId();
        handler.register(id, listener, types);
        return id;
    }

    @Override
    public void delete(int id) {
        final Iterator<ProfilerTransformer> it = transformers.iterator();
        Set<String> patterns = new HashSet<>();
        while (it.hasNext()) {
            final ProfilerTransformer next = it.next();
            if (id == next.getId()) {
                handler.unRegister(next.getId());
                inst.removeTransformer(next);
                it.remove();
                patterns.add(next.getPattern());
            }
        }
        // TODO 查找transform了的class
        Set<Class<?>> classes = new HashSet<>();
        try {
            inst.retransformClasses(classes.toArray(new Class[0]));
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
    }
}
