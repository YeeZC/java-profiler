package me.zyee.java.profiler.report.markdown;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class Table extends BaseNode {
    private final List<String> header;
    private final List<List<String>> data;

    private Table(Builder builder) {
        super(builder);
        this.header = builder.header;
        this.data = builder.data;
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
        if (!header.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            StringBuilder split = new StringBuilder();
            for (String h : header) {
                builder.append("| ").append(h);
                split.append("| --- ");
            }
            builder.append("|");
            split.append("|");
            joiner.add(builder).add(split);
        }
        if (!data.isEmpty()) {
            int size = -1;
            for (List<String> datum : data) {
                if (size == -1) {
                    size = datum.size();
                }
                if (size != datum.size()) {
                    throw new RuntimeException("Data size error");
                }
                StringBuilder builder = new StringBuilder();
                for (String d : datum) {
                    builder.append("| ").append(d);
                }
                builder.append("|");
                joiner.add(builder);
            }
        }
        return joiner.toString();
    }


    public List<String> getHeader() {
        return header;
    }

    public List<List<String>> getData() {
        return data;
    }

    public static class Builder extends BaseNode.Builder<Builder> {
        private List<String> header = new ArrayList<>();
        private List<List<String>> data = new ArrayList<>();

        private Builder() {
        }

        public Builder setHeader(List<String> header) {
            this.header = header;
            return this;
        }

        public Builder setHeader(String... header) {
            this.header = Lists.newArrayList(header);
            return this;
        }

        public Builder addRow(List<String> data) {
            this.data.add(data);
            return this;
        }

        public Builder addRow(String... data) {
            this.data.add(Lists.newArrayList(data));
            return this;
        }

        public Builder of(Table table) {
            this.header = table.header;
            this.data = table.data;
            return this;
        }

        public Table build() {
            return new Table(this);
        }
    }
}
