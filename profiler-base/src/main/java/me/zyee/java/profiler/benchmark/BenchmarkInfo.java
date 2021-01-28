package me.zyee.java.profiler.benchmark;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import me.zyee.java.profiler.utils.StringHelper;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/26
 */
public class BenchmarkInfo {
    private final List<Param> params;
    private final String mode;
    private final long count;
    private final double score;
    private final double error;
    private final TimeUnit unit;

    private BenchmarkInfo(Builder builder) {
        this.params = builder.params;
        this.mode = builder.mode;
        this.count = builder.count;
        this.score = builder.score;
        this.error = builder.error;
        this.unit = builder.unit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Param> getParams() {
        return params;
    }

    public <T> T getMode(Class<T> enumType) {
        if (null == enumType) {
            return (T) mode;
        }
        if (enumType.isEnum()) {
            return (T) Enum.valueOf((Class) enumType, mode);
        } else if (enumType == String.class) {
            return enumType.cast(mode);
        }
        throw new UnsupportedOperationException();
    }

    public long getCount() {
        return count;
    }

    public double getScore() {
        return score;
    }

    public double getError() {
        return error;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public static class Param {
        public String name;
        public String value;
    }


    public static class Builder {
        private List<Param> params;
        private String mode;
        private long count;
        private double score;
        private double error;
        private TimeUnit unit;

        private Builder() {
        }

        public Builder setParams(List<Param> params) {
            this.params = params;
            return this;
        }

        public Builder setMode(String mode) {
            this.mode = mode;
            return this;
        }

        public Builder setCount(long count) {
            this.count = count;
            return this;
        }

        public Builder setScore(double score) {
            this.score = score;
            return this;
        }

        public Builder setError(double error) {
            this.error = error;
            return this;
        }

        public Builder setUnit(TimeUnit unit) {
            this.unit = unit;
            return this;
        }

        public Builder of(BenchmarkInfo benchmarkInfo) {
            this.params = benchmarkInfo.params;
            this.mode = benchmarkInfo.mode;
            this.count = benchmarkInfo.count;
            this.score = benchmarkInfo.score;
            this.error = benchmarkInfo.error;
            this.unit = benchmarkInfo.unit;
            return this;
        }

        public BenchmarkInfo build() {
            return new BenchmarkInfo(this);
        }
    }
}
