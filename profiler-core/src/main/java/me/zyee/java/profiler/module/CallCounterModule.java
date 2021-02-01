package me.zyee.java.profiler.module;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import javax.annotation.Resource;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.DefaultBehaviorFilter;
import me.zyee.java.profiler.impl.ContextHelper;
import me.zyee.java.profiler.utils.Matcher;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/29
 */
public class CallCounterModule implements Module {
    private final Set<Integer> watchIds;
    private final Class<?> clazz;
    private final Matcher<String> callClass;
    private final Matcher<String> callMethod;
    private final Function<Method, String> methodProceed;

    @Resource
    private EventWatcher watcher;

    public CallCounterModule(Class<?> clazz, String invokePattern, Function<Method, String> methodProceed) {
        this.clazz = clazz;
        this.methodProceed = methodProceed;
        final String[] split = invokePattern.split("#");
        this.callClass = Matcher.classNameMatcher(split[0]);
        this.callMethod = Matcher.classNameMatcher(split.length > 1 ? split[1] : "*");
        this.watchIds = new HashSet<>(this.clazz.getDeclaredMethods().length);
    }

    @Override
    public void enable() {
        final Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            final String apply = methodProceed.apply(method);
            if (StringUtils.isEmpty(apply)) {
                continue;
            }
            final AtomicInteger c = new AtomicInteger();
            final AtomicInteger value = ContextHelper.COUNTER.putIfAbsent(apply, c);
            final int watch = watcher.watch(new DefaultBehaviorFilter(clazz.getName() + "#" + method.getName()),
                    (s, s1, s2) -> callClass.matching(s) && callMethod.matching(s1),
                    event -> {
                        if (event != Event.Entry) {
                            if (value == null) {
                                c.incrementAndGet();
                            } else {
                                value.incrementAndGet();
                            }
                        }
                        return false;
                    }, Event.Type.CALL_BEFORE);
            watchIds.add(watch);
        }

    }

    @Override
    public void disable() {
        watchIds.forEach(watcher::delete);
    }

    @Override
    public EventWatcher getWatcher() {
        return watcher;
    }
}
