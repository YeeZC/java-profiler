package me.zyee.java.profiler;

/**
 * Profiler 粒度
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public enum Granularity {
    /**
     * 用例集
     */
    SUITE,
    /**
     * 用例
     */
    TEST_CASE,
    /**
     * 用例方法
     */
    TEST_CASE_METHOD;
}
