package me.zyee.java.profiler.filter;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/25
 */
public interface CallBeforeFilter {
    boolean methodFilter(String caller, String name, String descriptor);

    CallBeforeFilter TRUE = ((caller, name, descriptor) -> true);
    CallBeforeFilter FALSE = ((caller, name, descriptor) -> false);
}
