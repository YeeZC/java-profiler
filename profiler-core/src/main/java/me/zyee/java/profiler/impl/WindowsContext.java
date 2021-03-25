package me.zyee.java.profiler.impl;

import java.util.Queue;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.Profiler;
import me.zyee.java.profiler.win.WindowsProfiler;

/**
 * TODO WindowsContext
 *
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/9
 */
class WindowsContext extends BaseContext {
    private final Profiler profiler;

    WindowsContext(String name) {
        super(name);
        this.profiler = new WindowsProfiler();
    }

    @Override
    public Profiler getProfiler() {
        return this.profiler;
    }

    @Override
    public Context resolve(String name) {
        Context ctx = this;
        final Profiler profiler = new WindowsProfiler();
        return new Context() {
            @Override
            public Profiler getProfiler() {
                return profiler;
            }

            @Override
            public Queue<ProfileItem> getProfileItems() {
                return ctx.getProfileItems();
            }

            @Override
            public Context resolve(String name) {
                return this;
            }

            @Override
            public String name() {
                return null;
            }
        };
    }
}
