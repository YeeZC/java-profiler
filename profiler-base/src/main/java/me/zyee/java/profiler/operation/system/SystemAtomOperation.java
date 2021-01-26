package me.zyee.java.profiler.operation.system;

import java.util.function.Supplier;
import me.zyee.java.profiler.operation.AtomOperation;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/26
 */
public enum SystemAtomOperation implements AtomOperation {
    //
    BYTE_ARRAY_COPY,
    INT_ARRAY_COPY,
    LONG_ARRAY_COPY,
    DOUBLE_ARRAY_COPY;
    private String name;
    private long cost;
    private String pattern;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getCost() {
        return cost;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public long getWhen() {
        return 1;
    }

    @Override
    public long getExpect() {
        return 1;
    }

    @Override
    public Supplier<Long> getActual() {
        return null;
    }
}
