package me.zyee.java.profiler.bean;

import me.zyee.java.profiler.utils.FormatUtil;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/21
 */
public class IOSpeed {
    private final String name;
    private final IOSpeedType type;
    private final long speed;

    private IOSpeed(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.speed = builder.speed;
    }

    public static Builder builder() {
        return new Builder();
    }


    public String getName() {
        return name;
    }

    public IOSpeedType getType() {
        return type;
    }

    public long getSpeed() {
        return speed;
    }

    public static class Builder {
        private String name;
        private IOSpeedType type;
        private long speed;

        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(IOSpeedType type) {
            this.type = type;
            return this;
        }

        public Builder setSpeed(long speed) {
            this.speed = speed;
            return this;
        }

        public Builder of(IOSpeed iOSpeed) {
            this.name = iOSpeed.name;
            this.type = iOSpeed.type;
            this.speed = iOSpeed.speed;
            return this;
        }

        public IOSpeed build() {
            return new IOSpeed(this);
        }
    }

    @Override
    public String toString() {
        return "IOSpeed{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", speed=" + FormatUtil.formatValue(speed, "B/ms") +
                '}';
    }
}
