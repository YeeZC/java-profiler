package me.zyee.java.profiler.fork;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/14
 */
public class ForkJoiner {
    private final static ForkJoinPool POOL = new ForkJoinPool();

    public static <T> T invoke(RecursiveTask<T> task) {
        return POOL.invoke(task);
    }

    public static void shutdown() {
        POOL.shutdown();
    }
}
