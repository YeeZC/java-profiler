package me.zyee.java.profiler.report.markdown;

import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class List extends BaseNode {
    private final java.util.List<Item> items;
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
        items.stream().map(item -> renderItem(start, 0, item)).forEach(joiner::add);
        return joiner.toString();
    }

    private String renderItem(String prefix, int dept, Item item) {
        StringJoiner joiner = new StringJoiner("\n");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dept; i++) {
            builder.append("\t");
        }
        builder.append(prefix).append(item.text);
        joiner.add(builder);
        for (Item child : item.children) {
            joiner.add(renderItem(prefix, dept + 1, child));
        }
        return joiner.toString();
    }

    public java.util.List<Item> getItems() {
        return items;
    }

    public boolean getSort() {
        return sort;
    }

    public static class Builder extends BaseNode.Builder<Builder> {
        private java.util.List<Item> items;
        private boolean sort;

        private Builder() {
        }

        public Builder setItems(java.util.List<Item> items) {
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

    public static class Item {
        public String text;
        public final java.util.List<Item> children = new ArrayList<>();

        public static Item newItem(String text) {
            final Item item = new Item();
            item.text = text;
            return item;
        }

        public Item addChild(Item child) {
            children.add(child);
            return this;
        }
    }
}
