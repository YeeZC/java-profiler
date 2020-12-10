package me.zyee.java.profiler.impl;

import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.Profiler;
import me.zyee.java.profiler.posix.Format;
import me.zyee.java.profiler.posix.PosixProfiler;
import one.profiler.Counter;
import one.profiler.Events;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/9
 */
class PosixContext extends BaseContext {
    private final Profiler profiler;

    PosixContext(String name, Events event) {
        super(name);
        this.profiler = init(name, event);
    }

    private Profiler init(String name, Events event) {
        final Path profilerPath = PosixProfiler.TMP.resolve(name);
        if (!Files.exists(profilerPath)) {
            try {
                Files.createDirectories(profilerPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return PosixProfiler.builder()
                .setEvent(event)
                .setOutput(profilerPath.resolve(event.name).toString())
                .setThreads(true)
                .setFormat(Format.builder()
                        .setTree(Counter.TOTAL).build())
                .build();
    }

    @Override
    public Profiler getProfiler() {
        return profiler;
    }

    @Override
    public Context resolve(String name) {
        Context ctx = this;
        final Profiler cpu = init(this.name + File.separator + name, Events.CPU);
        return new Context() {
            @Override
            public Profiler getProfiler() {
                return cpu;
            }

            @Override
            public Queue<ProfileItem> getProfileItems() {
                return ctx.getProfileItems();
            }

            @Override
            public Context resolve(String name) {
                return this;
            }
        };
    }
}
