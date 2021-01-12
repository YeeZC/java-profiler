package me.zyee.java.profiler.bean;

import me.zyee.java.profiler.utils.FormatUtil;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/12
 */
public class Net {
    private final String name;
    private final long speed;

    private Net(Builder builder) {
        this.name = builder.name;
        this.speed = builder.speed;
    }

    public static Builder builder() {
        return new Builder();
    }


    public String getName() {
        return name;
    }

    public long getSpeed() {
        return speed;
    }

    public static class Builder {
        private String name;
        private Long speed;

        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSpeed(long speed) {
            this.speed = speed;
            return this;
        }

        public Builder of(Net net) {
            this.name = net.name;
            this.speed = net.speed;
            return this;
        }

        public Net build() {
            return new Net(this);
        }
    }

    @Override
    public String toString() {
        return "Net{" +
                "name='" + name + '\'' +
                ", speed=" + FormatUtil.formatValue(speed, "bps") +
                '}';
    }
}
