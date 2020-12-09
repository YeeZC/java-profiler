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

    PosixContext(String name) {
        super(name);
        this.profiler = init(name);
    }

    private Profiler init(String name) {
        final Path profilerPath = PosixProfiler.TMP.resolve(name);
        if (!Files.exists(profilerPath)) {
            try {
                Files.createDirectories(profilerPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Profiler flameProfiler = PosixProfiler.builder()
                .setEvent(Events.CPU)
                .setOutput(profilerPath.resolve("cpu.html").toString())
                .setThreads(true)
                .setFormat(Format.builder()
                        .setTree(Counter.TOTAL).build())
                .build();
        Profiler memoryProfiler = PosixProfiler.builder()
                .setEvent(Events.ALLOC)
                .setOutput(profilerPath.resolve("alloc.html").toString())
                .setThreads(true)
                .setFormat(Format.builder()
                        .setTree(Counter.TOTAL).build())
                .build();
        return new Profiler() {
            @Override
            public void start() {
                flameProfiler.start();
                memoryProfiler.start();
            }

            @Override
            public Path stop() {
                flameProfiler.stop();
                memoryProfiler.stop();
                return profilerPath;
            }
        };
    }

    @Override
    public Profiler getProfiler() {
        return profiler;
    }

    @Override
    public Context resolve(String name) {
        Context ctx = this;
        final Profiler profiler = init(this.name + File.separator + name);
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
        };
    }
}
