package me.zyee.java.profiler;

import java.util.function.Function;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public interface Task extends Function<Context, Result> {
}
