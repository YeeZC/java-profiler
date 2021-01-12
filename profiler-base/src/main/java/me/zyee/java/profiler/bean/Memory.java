package me.zyee.java.profiler.bean;

import java.util.Objects;
import me.zyee.java.profiler.utils.FormatUtil;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/12
 */
public class Memory {
    private final long clockSpeed;

    private Memory(Builder builder) {
        this.clockSpeed = Objects.requireNonNull(builder.clockSpeed, "clockSpeed");
    }

    public static Builder builder() {
        return new Builder();
    }

    public double getClockSpeed() {
        return clockSpeed;
    }

    public static class Builder {
        private Long clockSpeed;

        private Builder() {
        }

        public Builder setClockSpeed(long clockSpeed) {
            this.clockSpeed = clockSpeed;
            return this;
        }

        public Builder of(Memory memory) {
            this.clockSpeed = memory.clockSpeed;
            return this;
        }

        public Memory build() {
            return new Memory(this);
        }
    }

    @Override
    public String toString() {
        return "Memory{" +
                "clockSpeed=" + FormatUtil.formatHertz(clockSpeed) +
                '}';
    }
}
