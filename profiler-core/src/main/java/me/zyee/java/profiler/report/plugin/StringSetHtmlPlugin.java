package me.zyee.java.profiler.report.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/2/24
 */
public class StringSetHtmlPlugin implements HtmlPlugin {
    private final String title;
    private final List<String> data;

    private StringSetHtmlPlugin(Builder builder) {
        this.title = builder.title;
        this.data = builder.data;
    }

    public static Builder builder() {
        return new Builder();
    }


    @Override
    @JsonProperty
    public String getTitle() {
        return title;
    }

    @Override
    @JsonProperty
    public List<Object> getData() {
        return new ArrayList<>(data);
    }

    @Override
    @JsonProperty
    public List<HtmlTableColumn> getColumns() {
        return null;
    }

    @Override
    @JsonProperty
    public boolean isExpandable() {
        return false;
    }

    @Override
    public PluginType getType() {
        return PluginType.ol;
    }

    public static class Builder {
        private String title;
        private List<String> data;

        private Builder() {
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setData(List<String> data) {
            this.data = data;
            return this;
        }

        public Builder of(StringSetHtmlPlugin stringSetHtmlPlugin) {
            this.title = stringSetHtmlPlugin.title;
            this.data = stringSetHtmlPlugin.data;
            return this;
        }

        public StringSetHtmlPlugin build() {
            return new StringSetHtmlPlugin(this);
        }
    }
}
