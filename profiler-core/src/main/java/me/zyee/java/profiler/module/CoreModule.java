package me.zyee.java.profiler.module;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import me.zyee.java.profiler.WarmupSwitcher;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.BehaviorFilter;
import me.zyee.java.profiler.fork.ForkJoiner;
import me.zyee.java.profiler.fork.SearchTask;
import me.zyee.java.profiler.utils.Matcher;

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
    @Resource
    private WarmupSwitcher switcher;

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

    public static void entryWarmup() {
        getInstance().switcher.change(true);
    }

    public static void exitWarmup() {
        getInstance().switcher.change(false);
    }

    public static boolean isWarmup() {
        return getInstance().switcher.isWarmup();
    }
}
