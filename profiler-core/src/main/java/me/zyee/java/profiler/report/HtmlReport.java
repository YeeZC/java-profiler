package me.zyee.java.profiler.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import me.zyee.java.profiler.fork.ForkJoiner;
import me.zyee.java.profiler.report.plugin.HtmlPlugin;
import me.zyee.java.profiler.utils.FileUtils;
import net.lingala.zip4j.ZipFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.event.Level;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.RecursiveTask;
import java.util.zip.CRC32;

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
    private transient final String flameSrc;
    private transient static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final transient ThreadLocal<CRC32> crc32Local = ThreadLocal.withInitial(CRC32::new);

    private HtmlReport(Builder builder) {
        this.name = builder.name;
        this.plugins = builder.plugins;
        this.flameSrc = builder.flame;
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
            final String hex = Long.toHexString(value);
            final String js = "data." + hex + ".js";
            Files.write(dist.resolve(js), Lists.newArrayList("window.profileData=" + new String(bytes)));
            final Path html = dist.resolve(name + "." + hex + ".html");
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

    public List<FlameNode> getFlame() {
        return ForkJoiner.invoke(new FlameNodeTask(Jsoup.parse(flameSrc)
                .select("ul.tree>li"), new FlameNode())).children;
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


        public HtmlReport build() {
            return new HtmlReport(this);
        }
    }

    public static class FlameNode {
        private String title;
        private String search;
        private Level level;
        private List<FlameNode> children = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSearch() {
            return search;
        }

        public void setSearch(String search) {
            this.search = search;
        }

        public Level getLevel() {
            return level;
        }

        public void setLevel(Level level) {
            this.level = level;
        }

        public List<FlameNode> getChildren() {
            return children;
        }

        public void setChildren(List<FlameNode> children) {
            this.children = children;
        }
    }

    /**
     * @author yee
     * @version 1.0
     * Create by yee on 2021/3/10
     */
    public static class FlameNodeTask extends RecursiveTask<FlameNode> {
        private final Elements elements;
        private final FlameNode root;

        public FlameNodeTask(Elements elements, FlameNode root) {
            this.elements = elements;
            this.root = root;
        }

        @Override
        protected FlameNode compute() {
            for (Element element : elements) {
                Optional.ofNullable(element.selectFirst("span")).ifPresent(el -> {
                    final String span = el.text().replace("/", ".");
                    final String div = element.selectFirst("div").text();
                    final FlameNode node = new FlameNode();
                    node.setTitle(div);
                    node.setSearch(span);
                    node.setChildren(new ArrayList<>());
                    node.setLevel("red".equals(el.attr("class")) ? Level.ERROR : Level.INFO);
                    final Element ul = element.selectFirst("ul");
                    if (null != ul) {
                        root.getChildren().add(new FlameNodeTask(ul.children(), node).fork().join());
                    } else {
                        root.getChildren().add(node);
                    }

                });
            }
            return root;
        }
    }
}
