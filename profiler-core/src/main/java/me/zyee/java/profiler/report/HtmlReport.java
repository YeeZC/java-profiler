package me.zyee.java.profiler.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import javax.annotation.Resource;
import me.zyee.java.profiler.report.plugin.HtmlPlugin;
import me.zyee.java.profiler.utils.FileUtils;
import net.lingala.zip4j.ZipFile;

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
            final Path dist = path.resolve("dist");
            if (!Files.exists(dist)) {
                final File zip = File.createTempFile("dist", ".zip");
                try (InputStream is = HtmlReport.class.getResourceAsStream("/dist");
                     FileOutputStream fos = new FileOutputStream(zip)
                ) {
                    FileUtils.transfer(is, fos);
                    fos.close();
                    ZipFile file = new ZipFile(zip);
                    file.extractAll(path.toString());
                }
            }
            final String js = "data." + Long.toHexString(value) + ".js";
            Files.write(dist.resolve(js), Lists.newArrayList("window.profileData=" + new String(bytes)));
            final Path html = dist.resolve(name + "." + Long.toHexString(value) + ".html");
            Files.copy(dist.resolve("index.html"), html);
            final byte[] htmlData = Files.readAllBytes(html);
            Files.write(html, new String(htmlData).replace("data.js", js).getBytes(StandardCharsets.UTF_8));
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
