package me.zyee.java.profiler.theoretical;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public class Theoretical {
    private static final Queue<Supplier<Long>> queue = new ConcurrentLinkedQueue<>();
}
