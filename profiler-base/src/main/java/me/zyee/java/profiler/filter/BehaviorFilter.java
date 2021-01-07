package me.zyee.java.profiler.filter;

import java.util.stream.Stream;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/7
 */
public interface BehaviorFilter {
    /**
     * 过滤类名
     *
     * @param className 类名
     * @return 过滤结果
     */
    boolean classFilter(String className);

    /**
     * 过滤方法名
     *
     * @param methodName          方法名
     * @param annotationSignCode
     * @param strict              严格限定参数类型
     * @param paramsTypeClassName 参数类型
     * @return 返回过滤结果
     */
    boolean methodFilter(String methodName, Stream<String> annotationSignCode, boolean strict, String... paramsTypeClassName);

    /**
     * 默认不严格限定
     *
     * @param methodName          方法名
     * @param paramsTypeClassName 参数类型
     * @return 返回过滤结果
     */
    default boolean methodFilter(String methodName, Stream<String> annotationSignCode, String... paramsTypeClassName) {
        return methodFilter(methodName, annotationSignCode, false, paramsTypeClassName);
    }

    BehaviorFilter TRUE = new BehaviorFilter() {
        @Override
        public boolean classFilter(String className) {
            return true;
        }

        @Override
        public boolean methodFilter(String methodName, Stream<String> annotationSignCode, boolean strict, String... paramsTypeClassName) {
            return true;
        }
    };

    BehaviorFilter FALSE = new BehaviorFilter() {
        @Override
        public boolean classFilter(String className) {
            return false;
        }

        @Override
        public boolean methodFilter(String methodName, Stream<String> annotationSignCode, boolean strict, String... paramsTypeClassName) {
            return false;
        }
    };
}
