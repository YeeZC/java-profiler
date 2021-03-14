package me.zyee.java.profiler.operation.impl;

import me.zyee.java.profiler.Operation;
import me.zyee.java.profiler.operation.Summary;
import me.zyee.java.profiler.report.markdown.Node;

import java.util.Optional;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public class BaseOperation implements Operation {
    private final String name;
    private final long cost;
    private final String pattern;
    private final Summary summery;

    protected BaseOperation(BaseBuilder<?> builder) {
        this.name = builder.name;
        this.cost = builder.cost;
        this.pattern = builder.pattern;
        this.summery = builder.summery;
        if (this.summery instanceof LinkedSummary) {
            ((LinkedSummary) this.summery).setOperation(this);
        }
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
        return Optional.ofNullable(summery).map(Node::render).orElse(null);
    }

    protected static class BaseBuilder<T extends BaseBuilder<?>> {
        private String name;
        private long cost;
        private String pattern;
        private Summary summery;

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

        public T setSummery(Summary summery) {
            this.summery = summery;
            return (T) this;
        }

        public T setSummery(String summery) {
            this.summery = new SimpleSummary(summery);
            return (T) this;
        }

        public T setSummery(String summery, boolean linked) {
            this.summery = linked ? new LinkedSummary(summery) : new SimpleSummary(summery);
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
