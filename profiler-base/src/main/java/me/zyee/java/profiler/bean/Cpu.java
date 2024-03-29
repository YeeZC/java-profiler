package me.zyee.java.profiler.bean;

import java.util.Objects;
import me.zyee.java.profiler.utils.FormatUtil;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/11
 */
public class Cpu {
    private final String vendor;
    private final long freq;
    private final int logical;
    private final int physical;

    private Cpu(Builder builder) {
        this.freq = Objects.requireNonNull(builder.freq, "freq");
        this.logical = Objects.requireNonNull(builder.logical, "logical");
        this.physical = Objects.requireNonNull(builder.physical, "physical");
        this.vendor = Objects.requireNonNull(builder.vendor, "vendor");
    }

    public static Builder builder() {
        return new Builder();
    }


    public long getFreq() {
        return freq;
    }

    public int getLogical() {
        return logical;
    }

    public int getPhysical() {
        return physical;
    }

    public String getVendor() {
        return vendor;
    }

    public static class Builder {
        private Long freq;
        private Integer logical;
        private Integer physical;
        private String vendor;

        private Builder() {
        }

        public Builder setFreq(long freq) {
            this.freq = freq;
            return this;
        }

        public Builder setLogical(int logical) {
            this.logical = logical;
            return this;
        }

        public Builder setPhysical(int physical) {
            this.physical = physical;
            return this;
        }

        public Builder setVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        public Builder of(Cpu cpu) {
            this.freq = cpu.freq;
            this.logical = cpu.logical;
            this.physical = cpu.physical;
            this.vendor = cpu.vendor;
            return this;
        }

        public Cpu build() {
            return new Cpu(this);
        }
    }

    @Override
    public String toString() {
        return "Cpu{" +
                "freq=" + FormatUtil.formatHertz(freq) +
                ", logical=" + logical +
                ", physical=" + physical +
                '}';
    }
}
