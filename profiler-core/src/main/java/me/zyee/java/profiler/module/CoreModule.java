package me.zyee.java.profiler.module;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import me.zyee.java.profiler.bean.Cpu;
import me.zyee.java.profiler.bean.Net;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.BehaviorFilter;
import me.zyee.java.profiler.fork.ForkJoiner;
import me.zyee.java.profiler.fork.SearchTask;
import me.zyee.java.profiler.utils.Matcher;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/12
 */
public class CoreModule {
    @Resource
    private Instrumentation inst;
    @Resource
    private EventWatcher watcher;
    @Resource(name = "nets")
    private List<Net> nets;
    @Resource
    private Cpu cpu;

    private final List<Integer> watches = new ArrayList<>();


    private static class SingletonHolder {
        private static final CoreModule INSTANCE = new CoreModule();
    }

    public static CoreModule getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static Set<Class<?>> find(String pattern) {
        return find(Matcher.classNameMatcher(pattern));
    }

    public static Set<Class<?>> find(Matcher<String> matcher) {
        final Class<?>[] classes = getInstance().inst.getAllLoadedClasses();
        return ForkJoiner.invoke(new SearchTask(classes, matcher));
    }

    public static void watch(BehaviorFilter filter, EventListener listener, Event.Type... types) {
        final int watch = getInstance().watcher.watch(filter, listener, types);
        getInstance().watches.add(watch);
    }

    public static void watch(BehaviorFilter filter, EventListener listener) {
        watch(filter, listener, Event.Type.values());
    }

    public static void destroy() {
        for (Integer watch : getInstance().watches) {
            getInstance().watcher.delete(watch);
        }
        ForkJoiner.shutdown();
    }

    public List<Net> getNets() {
        return nets;
    }

    public Cpu getCpu() {
        return cpu;
    }

    public static <T extends Module> T enableModule(T module) {
        final EventWatcher watcher = getInstance().watcher;
        final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(module.getClass(), Resource.class);
        for (Field field : fields) {
            if (ClassUtils.isAssignable(field.getType(), EventWatcher.class)
                    || ClassUtils.isAssignable(EventWatcher.class, field.getType())) {
                try {
                    FieldUtils.writeField(field, module, watcher, true);
                } catch (IllegalAccessException ignore) {
                }
            }
        }
        module.enable();
        return module;
    }
}
