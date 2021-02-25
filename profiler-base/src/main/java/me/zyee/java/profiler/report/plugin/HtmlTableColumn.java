package me.zyee.java.profiler.report.plugin;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/2/24
 */
public class HtmlTableColumn {
    private final String title;
    private final String key;
    private final String dataIndex;

    private HtmlTableColumn(Builder builder) {
        this.title = builder.title;
        this.key = builder.key;
        this.dataIndex = builder.dataIndex;
    }

    public static Builder builder() {
        return new Builder();
    }


    public String getTitle() {
        return title;
    }

    public String getKey() {
        return key;
    }

    public String getDataIndex() {
        return dataIndex;
    }

    public static class Builder {
        private String title;
        private String key;
        private String dataIndex;

        private Builder() {
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setKey(String key) {
            this.key = key;
            this.dataIndex = key;
            return this;
        }

        public Builder setDataIndex(String dataIndex) {
            this.dataIndex = dataIndex;
            return this;
        }

        public Builder of(HtmlTableColumn htmlTableColumn) {
            this.title = htmlTableColumn.title;
            this.key = htmlTableColumn.key;
            this.dataIndex = htmlTableColumn.dataIndex;
            return this;
        }

        public HtmlTableColumn build() {
            return new HtmlTableColumn(this);
        }
    }
}
