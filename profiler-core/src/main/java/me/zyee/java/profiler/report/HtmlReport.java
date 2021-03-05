package me.zyee.java.profiler.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.concurrent.RecursiveTask;
import me.zyee.java.profiler.fork.ForkJoiner;
import me.zyee.java.profiler.report.plugin.HtmlPlugin;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/3/2
 */
public class HtmlReport {
    private final String name;
    @Resource(name = "system")
    private transient List<HtmlPlugin> system;
    private final List<HtmlPlugin> plugins;
    private final String flame;
    private transient static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final transient ThreadLocal<CRC32> crc32Local = ThreadLocal.withInitial(CRC32::new);

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
            final CRC32 crc32 = crc32Local.get();
            crc32.update(bytes);
            final long value = crc32.getValue();
            Files.write(path.resolve("data." + value + ".js"), Lists.newArrayList("window.profileData=" + new String(bytes)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            crc32Local.remove();
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
            this.flame = flame.replace("</", "<#")
                                .replace("/", ".")
                                .replace("<#", "</");
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
