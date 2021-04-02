package me.zyee.java.profiler.operation;

import me.zyee.java.profiler.operation.impl.BaseOperation;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/4/2
 */
public class BenchmarkAtomOperation extends BaseOperation implements AtomOperation {
    private final Supplier<Long> actual;
    private final Class<?> benchmarkClass;
    private final AtomicBoolean init = new AtomicBoolean(false);


    private BenchmarkAtomOperation(Builder builder) {
        super(builder);
        this.actual = Objects.requireNonNull(builder.actual, "expect");
        this.benchmarkClass = Objects.requireNonNull(builder.benchmarkClass, "benchmarkClass");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public long getWhen() {
        return 1L;
    }

    @Override
    public long getExpect() {
        return 1L;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    @Override
    public Supplier<Long> getActual() {
        return actual;
    }

    public Class<?> getBenchmarkClass() {
        return benchmarkClass;
    }

    public boolean init() {
        return init.compareAndSet(false, true);
    }

    public static class Builder extends BaseBuilder<Builder> {
        private Supplier<Long> actual = () -> 0L;
        private Class<?> benchmarkClass;

        private Builder() {
        }

        public Builder setActual(Supplier<Long> actual) {
            this.actual = actual;
            return this;
        }

        public Builder setBenchmarkClass(Class<?> benchmarkClass) {
            this.benchmarkClass = benchmarkClass;
            return this;
        }

        public BenchmarkAtomOperation build() {
            return new BenchmarkAtomOperation(this);
        }
    }

    @Override
    public String toString() {
        return super.toString() + "BenchmarkAtomOperation{" +
                "benchmarkClass=" + benchmarkClass +
                ", actual=" + actual +
                '}';
    }
}
