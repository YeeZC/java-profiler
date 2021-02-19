package me.zyee.java.profiler.filter;

import java.util.stream.Stream;
import me.zyee.java.profiler.annotation.Profile;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/7
 */
public class ProfileBehaviorFilter extends DefaultBehaviorFilter {

    public ProfileBehaviorFilter() {
        super("*");
    }

    @Override
    public boolean methodFilter(String methodName, Stream<String> annotationSignCode, boolean strict, String... paramsTypeClassName) {
        return super.methodFilter(methodName, annotationSignCode, strict, paramsTypeClassName)
                && annotationSignCode.anyMatch(code -> code.equals(Profile.class.getName()));
    }
}
