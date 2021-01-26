package me.zyee.java.profiler.operation.impl;

import me.zyee.java.profiler.operation.AtomGroup;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/26
 */
public abstract class BaseAtomGroup implements AtomGroup {
    private final String name;
    private final String pattern;

    public BaseAtomGroup(String name, String pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public String getName() {
        return name;
    }


}
