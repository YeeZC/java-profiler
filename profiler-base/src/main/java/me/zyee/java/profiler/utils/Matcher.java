package me.zyee.java.profiler.utils;

/**
 * 匹配器
 * Created by vlinux on 15/5/17.
 */
public interface Matcher<T> {
    Matcher<?> TRUE = target -> true;
    Matcher<?> FALSE = target -> false;

    /**
     * 是否匹配
     *
     * @param target 目标字符串
     * @return 目标字符串是否匹配表达式
     */
    boolean matching(T target);

}
