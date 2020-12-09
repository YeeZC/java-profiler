package me.zyee.java.profiler;

import java.util.Queue;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public interface Context {
    FlameProfiler getProfiler();

    Queue<ProfileItem> getProfileItems();
}
