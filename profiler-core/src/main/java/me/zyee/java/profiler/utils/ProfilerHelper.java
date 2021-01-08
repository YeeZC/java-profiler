package me.zyee.java.profiler.utils;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Resource;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.BehaviorFilter;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/7
 */
public class ProfilerHelper {

    @Resource
    private Instrumentation inst;
    @Resource
    private EventWatcher watcher;
    private final List<Integer> watches = new ArrayList<>();

    private static class SingletonHolder {
        private static final ProfilerHelper instance = new ProfilerHelper();
    }

    public static ProfilerHelper getInstance() {
        return SingletonHolder.instance;
    }

    public static Set<Class<?>> find(String pattern) {
        return find(SearchUtils.classNameMatcher(pattern));
    }

    public static Set<Class<?>> find(Matcher<String> matcher) {
        final Class<?>[] allLoadedClasses = SingletonHolder.instance.inst.getAllLoadedClasses();
        return SearchUtils.searchClass(() -> Stream.of(allLoadedClasses), matcher);
    }

    public static void watch(BehaviorFilter filter, EventListener listener, Event.Type... types) {
        final int watch = SingletonHolder.instance.watcher.watch(filter, listener, types);
        SingletonHolder.instance.watches.add(watch);
    }

    public static void watch(BehaviorFilter filter, EventListener listener) {
        watch(filter, listener, Event.Type.values());
    }

    public static void destroy() {
        for (Integer watch : getInstance().watches) {
            getInstance().watcher.delete(watch);
        }
    }
}
