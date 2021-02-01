package me.zyee.java.profiler.agent;

import java.util.concurrent.atomic.AtomicBoolean;
import me.zyee.java.profiler.WarmupSwitcher;
import me.zyee.java.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.java.profiler.spy.Spy;
import me.zyee.java.profiler.spy.SpyHandler;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/2/1
 */
public class DefaultWarmupSwitcher implements WarmupSwitcher {
    private final SpyHandler delegate;
    private final AtomicBoolean warmup = new AtomicBoolean(true);
    private final DefaultEventHandler system = new DefaultEventHandler();


    public DefaultWarmupSwitcher(SpyHandler delegate) {
        this.delegate = delegate;
        Spy.init(system);
    }

    @Override
    public void change(boolean warmup) {
        if (warmup) {
            Spy.init(system);
        } else {
            Spy.init(delegate);
        }
        this.warmup.set(warmup);
    }

    public DefaultEventHandler getSystem() {
        return system;
    }

    @Override
    public boolean isWarmup() {
        return warmup.get();
    }
}
