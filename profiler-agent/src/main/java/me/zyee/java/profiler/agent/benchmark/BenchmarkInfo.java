package me.zyee.java.profiler.agent.benchmark;


import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/26
 */
public class BenchmarkInfo {
    private final List<Param> params;
    private final Mode mode;
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

    public Mode getMode() {
        return mode;
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

        @Override
        public String toString() {
            return "Param{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }


    public static class Builder {
        private List<Param> params;
        private Mode mode;
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

        public Builder setMode(Mode mode) {
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

        public Builder of(RunResult result) {
            final BenchmarkParams params = result.getParams();
            this.params = params.getParamsKeys().stream().map(key -> {
                Param param = new Param();
                param.name = key;
                param.value = params.getParam(key);
                return param;
            }).collect(Collectors.toList());
            this.mode = params.getMode();
            final Result pres = result.getPrimaryResult();
            this.count = pres.getSampleCount();
            this.score = pres.getScore();
            this.error = pres.getScoreError();
            this.unit = params.getTimeUnit();
            return this;
        }

        public BenchmarkInfo build() {
            return new BenchmarkInfo(this);
        }
    }

    @Override
    public String toString() {
        return "BenchmarkInfo{" +
                ", params=" + params +
                ", mode=" + mode +
                ", count=" + count +
                ", score=" + score +
                ", error=" + error +
                ", unit=" + unit +
                '}';
    }
}
