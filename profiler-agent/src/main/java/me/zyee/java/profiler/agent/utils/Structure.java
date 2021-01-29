package me.zyee.java.profiler.agent.utils;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import me.zyee.java.profiler.filter.BehaviorFilter;
import me.zyee.java.profiler.utils.Matcher;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/19
 */
public interface Structure {
    String getJavaClassName();

    boolean isNeedTransformer(BehaviorFilter filter);

    Set<String> getMatchesBehaviors(BiPredicate<String, Stream<String>> predicate);
}
