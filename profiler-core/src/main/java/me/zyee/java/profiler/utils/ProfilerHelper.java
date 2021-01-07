package me.zyee.java.profiler.utils;

import java.lang.instrument.Instrumentation;
import java.util.Set;
import java.util.stream.Stream;
import me.zyee.java.profiler.event.watcher.EventWatcher;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/7
 */
public class ProfilerHelper {
    private static Instrumentation inst;
    public static EventWatcher watcher;

    public static Set<Class<?>> find(String pattern) {
        return find(SearchUtils.classNameMatcher(pattern));
    }

    public static Set<Class<?>> find(Matcher<String> matcher) {
        final Class<?>[] allLoadedClasses = inst.getAllLoadedClasses();
        return SearchUtils.searchClass(() -> Stream.of(allLoadedClasses), matcher);
    }
}
