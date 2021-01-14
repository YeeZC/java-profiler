package me.zyee.java.profiler.flame;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.zyee.java.profiler.fork.ForkJoiner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/14
 */
public class ThreadFlameParser {
    private final int less;
    private final Path path;

    private ThreadFlameParser(Builder builder) {
        this.less = builder.less;
        this.path = builder.path;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void parse() throws IOException {
        final Document parse = Jsoup.parse(path.toFile(), "UTF-8");
        final Elements select = parse.select("ul.tree>li");
        final FlameNode invoke = ForkJoiner.invoke(new FlameNodeTask(less, select));
        System.out.println(invoke);
    }

    public static void main(String[] args) throws IOException {
        ThreadFlameParser.builder().setPath(Paths.get("/Users/yee/cpu.html")).build().parse();
    }

    public int getLess() {
        return less;
    }

    public Path getPath() {
        return path;
    }

    public static class Builder {
        private int less = 1;
        private Path path;

        private Builder() {
        }

        public Builder setLess(int less) {
            this.less = less;
            return this;
        }

        public Builder setPath(Path path) {
            this.path = path;
            return this;
        }

        public Builder of(ThreadFlameParser threadFlameParser) {
            this.less = threadFlameParser.less;
            this.path = threadFlameParser.path;
            return this;
        }

        public ThreadFlameParser build() {
            return new ThreadFlameParser(this);
        }
    }
}
