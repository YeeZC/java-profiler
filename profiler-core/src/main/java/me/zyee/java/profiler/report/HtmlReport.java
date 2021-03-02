package me.zyee.java.profiler.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.annotation.Resource;
import me.zyee.java.profiler.report.plugin.HtmlPlugin;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/3/2
 */
public class HtmlReport {
    private final String name;
    @Resource(name = "system")
    private transient List<HtmlPlugin> system;
    private List<HtmlPlugin> plugins;
    private final String flame;
    private transient static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private HtmlReport(Builder builder) {
        this.name = builder.name;
        this.plugins = builder.plugins;
        this.flame = builder.flame;
    }

    public void output(Path path) {
        for (int i = 0; i < system.size(); i++) {
            plugins.add(i, system.get(i));
        }
        try {
            final byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(this);
            final String hash = Base64.getEncoder().encodeToString(bytes).substring(0, 7);
            Files.write(path.resolve("data." + hash + ".js"), Lists.newArrayList("window.profileData=" + new String(bytes)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public List<HtmlPlugin> getPlugins() {
        return plugins;
    }

    public String getFlame() {
        return flame;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private List<HtmlPlugin> plugins = new ArrayList<>();
        private String flame;

        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPlugins(List<HtmlPlugin> plugins) {
            this.plugins = plugins;
            return this;
        }

        public Builder setFlame(String flame) {
            this.flame = flame;
            return this;
        }

        public Builder of(HtmlReport htmlReport) {
            this.name = htmlReport.name;
            this.plugins = htmlReport.plugins;
            this.flame = htmlReport.flame;
            return this;
        }

        public HtmlReport build() {
            return new HtmlReport(this);
        }
    }
}
