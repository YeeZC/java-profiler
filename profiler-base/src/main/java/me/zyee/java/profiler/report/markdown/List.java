package me.zyee.java.profiler.report.markdown;

import java.util.StringJoiner;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class List extends BaseNode {
    private final java.util.List<String> items;
    private final boolean sort;

    private List(Builder builder) {
        super(builder);
        this.items = builder.items;
        this.sort = builder.sort;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String render() {
        StringJoiner joiner = new StringJoiner("\n");
        if (null != getTitle()) {
            joiner.add(getTitle().render());
        }
        String start = sort ? "1. " : "- ";
        items.stream().map(item -> start + item).forEach(joiner::add);
        return joiner.toString();
    }

    public java.util.List<String> getItems() {
        return items;
    }

    public boolean getSort() {
        return sort;
    }

    public static class Builder extends BaseNode.Builder<Builder> {
        private java.util.List<String> items;
        private boolean sort;

        private Builder() {
        }

        public Builder setItems(java.util.List<String> items) {
            this.items = items;
            return this;
        }

        public Builder setSort(boolean sort) {
            this.sort = sort;
            return this;
        }

        public Builder of(List list) {
            this.items = list.items;
            this.sort = list.sort;
            return this;
        }

        public List build() {
            return new List(this);
        }
    }
}
