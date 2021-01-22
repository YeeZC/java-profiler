package me.zyee.java.profiler.report;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import me.zyee.java.profiler.report.markdown.Title;
import me.zyee.java.profiler.report.plugin.Plugin;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class Report {
    private final Title title;
    @Resource(name = "system")
    private List<Plugin> system;
    private final List<Plugin> contents;

    private Report(Builder builder) {
        this.title = Objects.requireNonNull(builder.title, "title");
        this.contents = Objects.requireNonNull(builder.contents, "contents");
    }

    public static Builder builder() {
        return new Builder();
    }

    public void output(Path output) throws IOException {
        List<String> data = Lists.newArrayList(title.render());
        system.stream().map(Plugin::render).forEach(data::add);
        contents.stream().map(Plugin::render).forEach(data::add);
        Files.write(output, data, StandardCharsets.UTF_8);
    }


    public Title getTitle() {
        return title;
    }

    public List<Plugin> getContents() {
        return contents;
    }

    public static class Builder {
        private Title title;
        private List<Plugin> contents = new ArrayList<>();

        private Builder() {
        }

        public Builder setTitle(Title title) {
            this.title = title;
            return this;
        }

        public Builder setContents(List<Plugin> contents) {
            this.contents = contents;
            return this;
        }

        public Builder addContents(Plugin... plugins) {
            this.contents.addAll(Lists.newArrayList(plugins));
            return this;
        }

        public Builder of(Report report) {
            this.title = report.title;
            this.contents = report.contents;
            return this;
        }

        public Report build() {
            return new Report(this);
        }
    }
}
