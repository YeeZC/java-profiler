package me.zyee.java.profiler.impl;

import java.util.Objects;
import me.zyee.java.profiler.AtomOperation;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public class DefaultAtomOperation extends BaseOperation implements AtomOperation {
    private final long when;
    private final long expect;

    private DefaultAtomOperation(Builder builder) {
        super(builder);
        this.when = Objects.requireNonNull(builder.when, "when");
        this.expect = Objects.requireNonNull(builder.expect, "expect");
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


    public static class Builder extends BaseBuilder<Builder> {
        private Long when;
        private Long expect;

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

        public Builder of(DefaultAtomOperation defaultAtomOperation) {
            this.when = defaultAtomOperation.when;
            this.expect = defaultAtomOperation.expect;
            return this;
        }

        public DefaultAtomOperation build() {
            return new DefaultAtomOperation(this);
        }
    }
}
