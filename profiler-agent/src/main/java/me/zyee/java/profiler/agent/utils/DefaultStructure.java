package me.zyee.java.profiler.agent.utils;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.zyee.java.profiler.filter.BehaviorFilter;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/19
 */
public class DefaultStructure implements Structure {
    private final ClassStructure delegate;

    public DefaultStructure(ClassStructure delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getJavaClassName() {
        return delegate.getJavaClassName();
    }

    @Override
    public boolean isNeedTransformer(BehaviorFilter filter) {
        boolean match = filter.classFilter(delegate.getJavaClassName());
        if (!match) {
            match = Optional.ofNullable(delegate.getSuperClassStructure()).map(ClassStructure::getJavaClassName)
                    .map(filter::classFilter).orElse(false);
        }
        if (!match) {
            match = delegate.getInterfaceClassStructures().stream().map(ClassStructure::getJavaClassName)
                    .anyMatch(filter::classFilter);
        }
        if (!match) {
            match = delegate.getFamilySuperClassStructures()
                    .stream().map(ClassStructure::getJavaClassName).anyMatch(filter::classFilter);
        }
        if (!match) {
            match = delegate.getFamilyInterfaceClassStructures()
                    .stream().map(ClassStructure::getJavaClassName).anyMatch(filter::classFilter);
        }
        return match;
    }

    @Override
    public Set<String> getMatchesBehaviors(BiPredicate<String, Stream<String>> predicate) {
        return delegate.getBehaviorStructures().stream().filter(stu -> predicate.test(stu.getName(),
                stu.getAnnotationTypeClassStructures().stream().map(ClassStructure::getJavaClassName)))
                .map(BehaviorStructure::getSignCode)
                .collect(Collectors.toSet());
    }
}
