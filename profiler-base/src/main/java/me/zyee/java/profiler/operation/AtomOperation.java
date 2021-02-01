package me.zyee.java.profiler.operation;

import java.util.function.Supplier;
import me.zyee.java.profiler.Operation;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public interface AtomOperation extends Operation {
    long getWhen();

    long getExpect();

    Supplier<Long> getActual();

    String getSummery();
}
