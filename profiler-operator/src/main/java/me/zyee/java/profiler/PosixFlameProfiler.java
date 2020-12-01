package me.zyee.java.profiler;

import one.profiler.AsyncProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public class PosixFlameProfiler implements FlameProfiler {
    private final Logger logger = LoggerFactory.getLogger(PosixFlameProfiler.class);
    private final AsyncProfiler profiler;
    private static final String DEFAULT_EVENT = "cpu";
    private static final String DEFAULT_INTERVAL = "10ms";
    private static final String DEFAULT_FORMAT = "tree=total";

    private String event = DEFAULT_EVENT;
    private String interval = DEFAULT_INTERVAL;
    private String format = DEFAULT_FORMAT;
    private String output;
    private boolean threads;
    private String include;
    private String exclude;

    public PosixFlameProfiler() {
        try (final InputStream is = Class.class.getResourceAsStream(getLibPath())) {
            final Path tmp = Files.createTempDirectory("me.zyee.java.profiler");
            final Path resolve = tmp.resolve("libasyncProfiler.so");
            Files.copy(is, resolve);
            profiler = AsyncProfiler.getInstance(resolve.toString());
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
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

    public AsyncProfiler getProfiler() {
        return profiler;
    }

    public String getOutput() {
        return output;
    }

    @Override
    public void setOutput(String output) {
        this.output = output;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public boolean isThreads() {
        return threads;
    }

    public void setThreads(boolean threads) {
        this.threads = threads;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getInclude() {
        return include;
    }

    @Override
    public void setInclude(String include) {
        this.include = include;
    }

    public String getExclude() {
        return exclude;
    }

    @Override
    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    @Override
    public void reset() {
        event = DEFAULT_EVENT;
        interval = DEFAULT_INTERVAL;
        format = DEFAULT_FORMAT;
        output = null;
        threads = false;
        include = null;
        exclude = null;
    }

    private String buildExecuteArgs(Action action) {
        StringBuilder builder = new StringBuilder();
        builder.append(action).append(",");
        builder.append("event=cpu,");
        if (StringUtils.isNotBlank(output)) {
            builder.append("file=").append(output).append(",");
        }
        if (this.threads) {
            builder.append("threads,");
        }
        if (StringUtils.isNoneBlank(format)) {
            builder.append(format).append(",");
        }
        if (StringUtils.isNotBlank(include)) {
            final String[] split = StringUtils.split(include, ",");
            for (String s : split) {
                builder.append("include=").append(s).append(",");
            }
        }
        if (StringUtils.isNotBlank(exclude)) {
            final String[] split = StringUtils.split(exclude, ",");
            for (String s : split) {
                builder.append("exclude=").append(s).append(",");
            }
        }
        builder.append("interval=").append(interval);
        logger.info("Async Profiler args = {}", builder);
        return builder.toString();
    }

    private String execute(AsyncProfiler asyncProfiler, Action action)
            throws IllegalArgumentException, IOException {
        String arg = buildExecuteArgs(action);
        String result = asyncProfiler.execute(arg);
        if (!result.endsWith("\n")) {
            result += "\n";
        }
        return result;
    }

    private enum Action {
        /**
         * 启动
         */
        start,
        /**
         * 停止
         */
        stop
    }

    private String getLibPath() {
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

}
