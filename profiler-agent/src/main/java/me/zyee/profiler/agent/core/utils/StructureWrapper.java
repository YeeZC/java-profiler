package me.zyee.profiler.agent.core.utils;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/19
 */
public class StructureWrapper implements Structure {
    private final ClassStructure delegate;

    public StructureWrapper(ClassStructure delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getJavaClassName() {
        return delegate.getJavaClassName();
    }

    @Override
    public Set<String> getMatchesBehaviors(BiPredicate<String, Stream<String>> predicate) {
        return delegate.getBehaviorStructures().stream().filter(stu -> predicate.test(stu.getName(),
                stu.getAnnotationTypeClassStructures().stream().map(ClassStructure::getJavaClassName)))
                .map(BehaviorStructure::getSignCode)
                .collect(Collectors.toSet());
    }
}
