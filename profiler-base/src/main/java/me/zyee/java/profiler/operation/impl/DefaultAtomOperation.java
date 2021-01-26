package me.zyee.java.profiler.operation.impl;

import java.util.Objects;
import java.util.function.Supplier;
import me.zyee.java.profiler.operation.AtomOperation;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public class DefaultAtomOperation extends BaseOperation implements AtomOperation {
    private final long when;
    private final long expect;
    private final Supplier<Long> actual;

    private DefaultAtomOperation(Builder builder) {
        super(builder);
        this.when = Objects.requireNonNull(builder.when, "when");
        this.expect = Objects.requireNonNull(builder.expect, "expect");
        this.actual = Objects.requireNonNull(builder.actual, "expect");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public long getWhen() {
        return when;
    }

    @Override
    public long getExpect() {
        return expect;
    }

    @Override
    public Supplier<Long> getActual() {
        return actual;
    }


    public static class Builder extends BaseBuilder<Builder> {
        private Long when = 1L;
        private Long expect = 1L;
        private Supplier<Long> actual = () -> 0L;

        private Builder() {
        }

        public Builder setWhen(long when) {
            this.when = when;
            return this;
        }

        public Builder setExpect(long expect) {
            this.expect = expect;
            return this;
        }

        public Builder setActual(Supplier<Long> actual) {
            this.actual = actual;
            return this;
        }

        public Builder of(DefaultAtomOperation defaultAtomOperation) {
            this.when = defaultAtomOperation.when;
            this.expect = defaultAtomOperation.expect;
            this.actual = defaultAtomOperation.actual;
            return this;
        }

        public DefaultAtomOperation build() {
            return new DefaultAtomOperation(this);
        }
    }

    @Override
    public String toString() {
        return super.toString() + "DefaultAtomOperation{" +
                "when=" + when +
                ", expect=" + expect +
                ", actual=" + actual +
                '}';
    }
}
