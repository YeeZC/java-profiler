package me.zyee.java.profiler.fork;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import me.zyee.java.profiler.utils.Matcher;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/14
 */
public class SearchTask extends RecursiveTask<Set<Class<?>>> {
    private final Class<?>[] source;
    private final int from;
    private final int to;
    private final Matcher<String> classNameMatcher;
    private final boolean searchSub;
    private final int limit;

    public SearchTask(Class<?>[] source, Matcher<String> classNameMatcher) {
        this(source, 0, source.length, classNameMatcher, true);
    }

    public SearchTask(Class<?>[] source, Matcher<String> classNameMatcher, boolean searchSub) {
        this(source, 0, source.length, classNameMatcher, searchSub);
    }

    public SearchTask(Class<?>[] source,
                      int from,
                      int to,
                      Matcher<String> classNameMatcher,
                      boolean searchSub) {
        this.source = source;
        this.from = from;
        this.to = to;
        this.classNameMatcher = classNameMatcher;
        this.searchSub = searchSub;
        this.limit = 100;
    }

    @Override
    protected Set<Class<?>> compute() {
        Set<Class<?>> matches = new HashSet<>(source.length);
        if (to - from > limit) {
            int count = source.length / limit;
            count += source.length % limit > 0 ? 1 : 0;
            for (int i = 0; i < count; i++) {
                final SearchTask task = new SearchTask(source,
                        i * limit,
                        Math.min((i + 1) * limit, source.length),
                        classNameMatcher,
                        searchSub);
                matches.addAll(task.fork().join());
            }
        } else {
            for (int i = from; i < to; i++) {
                final Class<?> clazz = source[i];
                if (classNameMatcher.matching(clazz.getName())) {
                    matches.add(clazz);
                }
            }
            if (searchSub) {
                final SubClassTask task = new SubClassTask(matches, 0, source.length);
                matches.addAll(task.fork().join());
            }
        }
        return matches;
    }

    private class SubClassTask extends RecursiveTask<Set<Class<?>>> {
        private final Set<Class<?>> supers;
        private final int from;
        private final int to;

        public SubClassTask(Set<Class<?>> supers, int from, int to) {
            this.supers = new HashSet<>(supers);
            this.from = from;
            this.to = to;
        }

        @Override
        protected Set<Class<?>> compute() {
            Set<Class<?>> matches = new HashSet<>(supers.size());
            if (to - from > limit) {
                int count = source.length / limit;
                count += source.length % limit > 0 ? 1 : 0;
                for (int i = 0; i < count; i++) {
                    final SubClassTask task = new SubClassTask(supers, i * limit,
                            Math.min((i + 1) * limit, source.length));
                    matches.addAll(task.fork().join());
                }
            } else {
                for (int i = from; i < to; i++) {
                    final Class<?> clazz = source[i];
                    for (Class<?> superClass : supers) {
                        if (superClass.isAssignableFrom(clazz) && clazz != superClass) {
                            matches.add(clazz);
                        }
                    }
                }
            }
            return matches;
        }
    }
}
