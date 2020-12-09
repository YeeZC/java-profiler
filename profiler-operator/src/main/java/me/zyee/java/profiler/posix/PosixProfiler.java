package me.zyee.java.profiler.posix;

import me.zyee.java.profiler.OS;
import me.zyee.java.profiler.Profiler;
import one.profiler.AsyncProfiler;
import one.profiler.Events;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public class PosixProfiler implements Profiler {
    private static final String DEFAULT_INTERVAL = "10ms";
    private static final Path LIB_FILE;
    public static final Path TMP;

    static {
        try (final InputStream is = Class.class.getResourceAsStream(getLibPath())) {
            TMP = Files.createTempDirectory("me.zyee.java.profiler");
            LIB_FILE = TMP.resolve("libasyncProfiler.so");
            if (!Files.exists(LIB_FILE)) {
                Files.copy(is, LIB_FILE);
            }
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }


    private static final Logger logger = LoggerFactory.getLogger(PosixProfiler.class);
    private final AsyncProfiler profiler;

    private final String args;
    private final String output;

    private PosixProfiler(Builder builder) {
        this.profiler = AsyncProfiler.getInstance(LIB_FILE.toString());
        this.output = builder.output;
        this.args = builder.args();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void start() {
        try {
            logger.info("Start Async Profiler");
            final String execute = execute(profiler, Action.start);
            logger.info("Execute Async Profiler finished {}", execute);
        } catch (Exception e) {
            logger.error("Execute Async Profiler failed", e);
        }
    }

    @Override
    public Path stop() {
        try {
            logger.info("Stop Async Profiler");
            final String execute = execute(profiler, Action.stop);
            logger.info("Stop Async Profiler finished {}", execute);
            return Paths.get(output);
        } catch (Exception e) {
            logger.error("Stop Async Profiler failed", e);
            return null;
        }
    }

    private String execute(AsyncProfiler asyncProfiler, Action action)
            throws IllegalArgumentException, IOException {
        final StringJoiner joiner = new StringJoiner(",");
        joiner.add(action.name()).add(args);
        String result = asyncProfiler.execute(joiner.toString());
        if (!result.endsWith("\n")) {
            result += "\n";
        }
        return result;
    }

    private static String getLibPath() {
        OS.OSType osType = OS.getOSType();
        StringJoiner join = new StringJoiner("-");
        switch (osType) {
            case Linux:
            case Macintosh:
                String property = System.getProperty("os.arch");
                if ("x86_64".equals(property) || "amd64".equals(property)) {
                    property = "x64";
                }
                return join.add("/async-profiler/libasyncProfiler")
                        .add(osType.simpleName)
                        .add(String.format("%s.so", property)).toString();
            default:
                throw new UnsupportedOperationException("Unsupported OS " + osType.name());
        }
    }

    public AsyncProfiler getProfiler() {
        return profiler;
    }

    public static class Builder {
        private Events event;
        private String interval;
        private Format format;
        private String output;
        private boolean threads;
        private String include;
        private String exclude;

        private Builder() {
        }

        public Builder setEvent(Events event) {
            this.event = event;
            return this;
        }

        public Builder setInterval(String interval) {
            this.interval = interval;
            return this;
        }

        public Builder setFormat(Format format) {
            this.format = format;
            return this;
        }

        public Builder setOutput(String output) {
            this.output = output;
            return this;
        }

        public Builder setThreads(boolean threads) {
            this.threads = threads;
            return this;
        }

        public Builder setInclude(String include) {
            this.include = include;
            return this;
        }

        public Builder setExclude(String exclude) {
            this.exclude = exclude;
            return this;
        }

        private String args() {
            StringJoiner joiner = new StringJoiner(",");
            joiner.add("event=" + Optional.ofNullable(event).map(e -> e.name).orElse(Events.CPU.name));
            if (StringUtils.isNotBlank(output)) {
                joiner.add("file=" + TMP.resolve(output));
            }
            if (this.threads) {
                joiner.add("threads");
            }
            final String format = discussFormat();
            if (StringUtils.isNoneBlank(format)) {
                joiner.add(format);
            }
            if (StringUtils.isNotBlank(include)) {
                final String[] split = StringUtils.split(include, ",");
                for (String s : split) {
                    joiner.add("include=" + s);
                }
            }
            if (StringUtils.isNotBlank(exclude)) {
                final String[] split = StringUtils.split(exclude, ",");
                for (String s : split) {
                    joiner.add("exclude=" + s);
                }
            }
            joiner.add("interval=" + Optional.ofNullable(interval).orElse(DEFAULT_INTERVAL));
            logger.info("Async Profiler args = {}", joiner);
            return joiner.toString();
        }

        private String discussFormat() {
            return Optional.ofNullable(format).map(f -> {
                StringJoiner joiner = new StringJoiner(",");
                if (f.getSummary()) {
                    joiner.add("summary");
                } else if (null != f.getSvg()) {
                    joiner.add("svg=" + f.getSvg().name);
                } else if (null != f.getTree()) {
                    joiner.add("tree=" + f.getTree().name);
                } else if (null != f.getCollapsed()) {
                    joiner.add("collapsed=" + f.getCollapsed().name);
                }
                if (f.getFlat() > 0) {
                    joiner.add("flat=" + f.getFlat());
                }
                if (f.getTraces() > 0) {
                    joiner.add("traces=" + f.getTraces());
                }

                return joiner.toString();
            }).orElse("");
        }

        public PosixProfiler build() {
            return new PosixProfiler(this);
        }
    }
}
