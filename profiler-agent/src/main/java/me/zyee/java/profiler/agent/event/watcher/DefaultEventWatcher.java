package me.zyee.java.profiler.agent.event.watcher;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.zyee.java.profiler.agent.event.handler.EventHandler;
import me.zyee.java.profiler.agent.transformer.ProfilerTransformer;
import me.zyee.java.profiler.agent.utils.ClassStructure;
import me.zyee.java.profiler.agent.utils.ClassStructureFactory;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.BehaviorFilter;
import me.zyee.java.profiler.filter.CallBeforeFilter;
import me.zyee.java.profiler.fork.ForkJoiner;
import me.zyee.java.profiler.fork.SearchTask;
import org.apache.commons.lang3.ClassUtils;

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
        return watch(filter, CallBeforeFilter.TRUE, listener, types);
    }

    @Override
    public int watch(BehaviorFilter pattern, EventListener listener) {
        return watch(pattern, listener, Event.Type.values());
    }

    @Override
    public int watch(BehaviorFilter filter, CallBeforeFilter callBefore, EventListener listener) {
        return watch(filter, callBefore, listener, Event.Type.values());
    }

    @Override
    public int watch(BehaviorFilter filter, CallBeforeFilter callBefore, EventListener listener, Event.Type... types) {
        final ProfilerTransformer transformer = new ProfilerTransformer(filter, callBefore, listener, types);
        transformers.add(transformer);
        Set<Class<?>> classes = ForkJoiner.invoke(new SearchTask(filter(inst.getAllLoadedClasses()),
                transformer.getFilter()::classFilter))
                .stream().filter(clazz -> {
                    final ClassStructure classStructure = ClassStructureFactory.createClassStructure(clazz);
                    return classStructure.getBehaviorStructures().stream().anyMatch(behavior ->
                            filter.methodFilter(behavior.getName(), behavior.getAnnotationTypeClassStructures()
                                    .stream()
                                    .map(ClassStructure::getJavaClassName)
                            ));
                }).collect(Collectors.toSet());
        final int id = transformer.getId();
        inst.addTransformer(transformer, true);
        handler.register(id, listener, types);
        try {
            if (!classes.isEmpty()) {
                inst.retransformClasses(classes.toArray(new Class[0]));
            }
        } catch (Throwable ignore) {
        }
        return id;
    }

    @Override
    public void delete(int id) {
        final Iterator<ProfilerTransformer> it = transformers.iterator();
        Set<String> transformed = new HashSet<>();
        while (it.hasNext()) {
            final ProfilerTransformer next = it.next();
            if (id == next.getId()) {
                handler.unRegister(next.getId());
                inst.removeTransformer(next);
                it.remove();
                transformed.addAll(next.getTransformed());
            }
        }

        if (!transformed.isEmpty()) {
            final Class<?>[] classes = transformed.stream().map(name -> {
                try {
                    return ClassUtils.getClass(name);
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }).filter(Objects::nonNull).toArray(Class[]::new);
            if (classes.length > 0) {
                try {
                    inst.retransformClasses(classes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Class<?>[] filter(Class<?>[] source) {
        return Stream.of(source).filter(clazz -> !(clazz.isArray() || clazz.isInterface() || clazz.isEnum() ||
                clazz.equals(Class.class) || clazz.equals(Method.class)
                || ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.equals(String.class)))
                .toArray(Class<?>[]::new);
    }
}
