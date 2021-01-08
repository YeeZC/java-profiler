package me.zyee.java.profiler.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileItem;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/9
 */
abstract class BaseContext implements Context {
    protected String name;

    private final Queue<ProfileItem> queue = new ConcurrentLinkedQueue<>();

    BaseContext(String name) {
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
