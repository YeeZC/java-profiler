package me.zyee.java.profiler.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import me.zyee.java.profiler.ProfilerCore;
import me.zyee.java.profiler.Runner;
import me.zyee.java.profiler.agent.MethodAgent;
import me.zyee.java.profiler.agent.StrictAgent;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class StrictProfilerCore implements ProfilerCore {
    private final Set<String> patterns;
    private final boolean regx;
    private final DefaultProfilerCore core;

    private StrictProfilerCore(Builder builder) {
        this.patterns = builder.patterns;
        this.regx = builder.regx;
        this.core = new DefaultProfilerCore();
        if (!this.patterns.isEmpty()) {
            StrictAgent.agentmain(patterns.toArray(new String[0]), MethodAgent.inst);
        }
    }

    public static Builder builder() {
        return new Builder();
    }


    @Override
    public void profile(Runner runner) throws IOException {
        core.profile(runner);
    }


    public Set<String> getPatterns() {
        return patterns;
    }


    public boolean getRegx() {
        return regx;
    }


    public static class Builder {
        private Set<String> patterns;
        private boolean regx;

        private Builder() {
            this.patterns = new HashSet<>(0);
        }

        public Builder addPattern(String pattern) {
            this.patterns.add(pattern);
            return this;
        }

        public Builder setRegx(boolean regx) {
            this.regx = regx;
            return this;
        }

        public Builder of(StrictProfilerCore strictProfilerCore) {
            this.patterns = strictProfilerCore.patterns;
            this.regx = strictProfilerCore.regx;
            return this;
        }

        public StrictProfilerCore build() {
            return new StrictProfilerCore(this);
        }
    }
}
