package me.zyee.java.profiler.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.Profiler;
import me.zyee.java.profiler.posix.Format;
import me.zyee.java.profiler.posix.PosixProfiler;
import one.profiler.Counter;
import one.profiler.Events;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/9
 */
class PosixContext extends BaseContext {
    private final Profiler profiler;
    private final Set<String> excludes;

    PosixContext(String name, Events event, Set<String> excludes) {
        super(name);
        this.excludes = Optional.ofNullable(excludes).map(HashSet::new).orElseGet(HashSet::new);
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
                .setOutput(profilerPath.resolve(event.name + ".html").toString())
                .setThreads(false)
                .setExclude(String.join(",", this.excludes))
                .setInterval("5ms")
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
        Profiler profiler;
        String targetName;
        if (StringUtils.isNotEmpty(this.name)) {
            targetName = this.name + File.separator + name;
        } else {
            targetName = name;
        }
        profiler = init(targetName, Events.CPU);
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
                return targetName;
            }
        };
    }
}
