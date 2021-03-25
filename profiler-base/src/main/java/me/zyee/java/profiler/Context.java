package me.zyee.java.profiler;

import java.util.Queue;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public interface Context {
    Profiler getProfiler();

    Queue<ProfileItem> getProfileItems();

    Context resolve(String name);

    String name();
}
