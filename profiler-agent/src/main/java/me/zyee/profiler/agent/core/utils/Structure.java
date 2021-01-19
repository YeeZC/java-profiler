package me.zyee.profiler.agent.core.utils;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/19
 */
public interface Structure {
    String getJavaClassName();

    Set<String> getMatchesBehaviors(BiPredicate<String, Stream<String>> predicate);
}
