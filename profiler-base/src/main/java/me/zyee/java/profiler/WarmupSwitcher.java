package me.zyee.java.profiler;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/2/1
 */
public interface WarmupSwitcher {
    void change(boolean warmup);

    boolean isWarmup();
}
