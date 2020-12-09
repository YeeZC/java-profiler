package me.zyee.java.profiler.posix;

import one.profiler.Counter;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/9
 */
public class Format {
    private final boolean summary;
    private final int traces;
    private final int flat;
    private final Counter collapsed;
    private final Counter svg;
    private final Counter tree;

    private Format(Builder builder) {
        this.summary = builder.summary;
        this.traces = builder.traces;
        this.flat = builder.flat;
        this.collapsed = builder.collapsed;
        this.svg = builder.svg;
        this.tree = builder.tree;
    }

    public static Builder builder() {
        return new Builder();
    }


    public boolean getSummary() {
        return summary;
    }

    public int getTraces() {
        return traces;
    }

    public int getFlat() {
        return flat;
    }


    public Counter getCollapsed() {
        return collapsed;
    }


    public Counter getSvg() {
        return svg;
    }


    public Counter getTree() {
        return tree;
    }

    public static class Builder {
        private boolean summary;
        private int traces;
        private int flat;
        private Counter collapsed;
        private Counter svg;
        private Counter tree;

        private Builder() {
        }

        public Builder setSummary(boolean summary) {
            this.summary = summary;
            return this;
        }

        public Builder setTraces(int traces) {
            this.traces = traces;
            return this;
        }

        public Builder setFlat(int flat) {
            this.flat = flat;
            return this;
        }

        public Builder setCollapsed(Counter collapsed) {
            this.collapsed = collapsed;
            return this;
        }

        public Builder setSvg(Counter svg) {
            this.svg = svg;
            return this;
        }

        public Builder setTree(Counter tree) {
            this.tree = tree;
            return this;
        }

        public Builder of(Format format) {
            this.summary = format.summary;
            this.traces = format.traces;
            this.flat = format.flat;
            this.collapsed = format.collapsed;
            this.svg = format.svg;
            this.tree = format.tree;
            return this;
        }

        public Format build() {
            return new Format(this);
        }
    }
}
