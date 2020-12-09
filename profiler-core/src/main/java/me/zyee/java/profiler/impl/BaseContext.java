package me.zyee.java.profiler.impl;

import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileItem;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/9
 */
abstract class BaseContext implements Context {
    protected String name;

    private final Queue<ProfileItem> queue = new ConcurrentLinkedQueue<>();

    public BaseContext(String name) {
        this.name = name;
    }

    @Override
    public Queue<ProfileItem> getProfileItems() {
        return queue;
    }

    @Override
    public Context resolve(String name) {
        return this;
    }
}
