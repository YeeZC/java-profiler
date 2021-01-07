package me.zyee.java.profiler.filter;

import me.zyee.java.profiler.utils.Matcher;
import me.zyee.java.profiler.utils.SearchUtils;

import java.util.stream.Stream;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/7
 */
public class DefaultBehaviorFilter implements BehaviorFilter {
    private final Matcher<String> classMatcher;
    private final Matcher<String> methodMatcher;
    private final String[] params;

    public DefaultBehaviorFilter(String pattern, boolean regx, String... params) {
        this.params = params;
        final String[] split = pattern.split("#");
        String classPattern = split[0];
        String methodPattern = null;
        if (split.length >= 2) {
            methodPattern = split[1];
        }
        this.classMatcher = SearchUtils.classNameMatcher(classPattern, regx);
        this.methodMatcher = SearchUtils.classNameMatcher(methodPattern, regx);
    }

    public DefaultBehaviorFilter(String pattern) {
        this(pattern, false);
    }

    @Override
    public boolean classFilter(String className) {
        return classMatcher.matching(className);
    }

    @Override
    public boolean methodFilter(String methodName, Stream<String> annotationSignCode, boolean strict, String... paramsTypeClassName) {
        return methodMatcher.matching(methodName) && matchParams(strict, paramsTypeClassName);
    }

    protected final boolean matchParams(boolean strict, String[] paramsTypeClassName) {
        if (strict) {
            if (params.length != paramsTypeClassName.length) {
                return false;
            }
            for (int i = 0; i < params.length; i++) {
                if (!params[i].equals(paramsTypeClassName[i])) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }
}
