package me.zyee.java.profiler.operation.impl;

import me.zyee.java.profiler.Operation;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public class BaseOperation implements Operation {
    private final String name;
    private final long cost;
    private final String pattern;
    private final String summery;

    protected BaseOperation(BaseBuilder<?> builder) {
        this.name = builder.name;
        this.cost = builder.cost;
        this.pattern = builder.pattern;
        this.summery = builder.summery;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getCost() {
        return cost;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public String getSummery() {
        return summery;
    }

    protected static class BaseBuilder<T extends BaseBuilder<?>> {
        private String name;
        private long cost;
        private String pattern;
        private String summery;

        public T setName(String name) {
            this.name = name;
            return (T) this;
        }

        public T setCost(long cost) {
            this.cost = cost;
            return (T) this;
        }

        public T setPattern(String pattern) {
            this.pattern = pattern;
            return (T) this;
        }

        public T setSummery(String summery) {
            this.summery = summery;
            return (T) this;
        }
    }

    @Override
    public String toString() {
        return "BaseOperation{" +
                "name='" + name + '\'' +
                ", cost=" + cost +
                ", pattern='" + pattern + '\'' +
                '}';
    }
}
