package me.zyee.java.profiler.operation.impl;

import java.util.ArrayList;
import java.util.List;
import me.zyee.java.profiler.Operation;
import me.zyee.java.profiler.operation.NormalOperation;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public class DefaultOperation extends BaseOperation implements NormalOperation {
    private final List<Operation> children = new ArrayList<>();

    private DefaultOperation(Builder builder) {
        super(builder);
        this.children.addAll(builder.children);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public List<Operation> getChildren() {
        return children;
    }

    public static class Builder extends BaseBuilder<Builder> {
        private final List<Operation> children;

        private Builder() {
            this.children = new ArrayList<>();
        }

        public Builder add(Operation operation) {
            this.children.add(operation);
            return this;
        }

        public Builder of(DefaultOperation defaultOperation) {
            this.children.addAll(defaultOperation.children);
            return this;
        }

        public DefaultOperation build() {
            return new DefaultOperation(this);
        }
    }
}
