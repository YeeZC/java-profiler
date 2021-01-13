package me.zyee.java.profiler.bean;

import java.util.function.LongSupplier;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/12
 */
public class Memory {
    private final long heapMax;
    private final LongSupplier heapUsed;
    private final long nonHeapMax;
    private final LongSupplier nonHeapUsed;
    private final long total;

    private Memory(Builder builder) {
        this.heapMax = builder.heapMax;
        this.heapUsed = builder.heapUsed;
        this.nonHeapMax = builder.nonHeapMax;
        this.nonHeapUsed = builder.nonHeapUsed;
        this.total = builder.total;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getHeapMax() {
        return heapMax;
    }

    public LongSupplier getHeapUsed() {
        return heapUsed;
    }

    public long getNonHeapMax() {
        return nonHeapMax;
    }

    public LongSupplier getNonHeapUsed() {
        return nonHeapUsed;
    }

    public long getTotal() {
        return total;
    }

    public static class Builder {
        private long heapMax;
        private LongSupplier heapUsed;
        private long nonHeapMax;
        private LongSupplier nonHeapUsed;
        private long total;

        private Builder() {
        }

        public Builder setHeapMax(long heapMax) {
            this.heapMax = heapMax;
            return this;
        }

        public Builder setHeapUsed(LongSupplier heapUsed) {
            this.heapUsed = heapUsed;
            return this;
        }

        public Builder setNonHeapMax(long nonHeapMax) {
            this.nonHeapMax = nonHeapMax;
            return this;
        }

        public Builder setNonHeapUsed(LongSupplier nonHeapUsed) {
            this.nonHeapUsed = nonHeapUsed;
            return this;
        }

        public Builder setTotal(long total) {
            this.total = total;
            return this;
        }

        public Builder of(Memory memory) {
            this.heapMax = memory.heapMax;
            this.heapUsed = memory.heapUsed;
            this.nonHeapMax = memory.nonHeapMax;
            this.nonHeapUsed = memory.nonHeapUsed;
            this.total = memory.total;
            return this;
        }

        public Memory build() {
            return new Memory(this);
        }
    }
}
