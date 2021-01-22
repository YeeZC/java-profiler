package me.zyee.java.profiler.report.plugin;

import com.google.common.collect.Lists;
import java.util.Set;
import java.util.StringJoiner;
import me.zyee.java.profiler.report.markdown.List;
import me.zyee.java.profiler.report.markdown.Title;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class ConclusionPlugin implements Plugin {
    private final Set<String> warnings;
    private final Set<String> errors;

    public ConclusionPlugin(Set<String> warnings, Set<String> errors) {
        this.warnings = warnings;
        this.errors = errors;
    }

    @Override
    public String render() {
        StringJoiner joiner = new StringJoiner("\n");
        return joiner.add(Title.builder().setTitle("结论").setLevel(2).build().render())
                .add(List.builder().setTitle(Title.builder().setTitle("警告").setLevel(3).build())
                        .setItems(Lists.newArrayList(warnings)).build().render())
                .add(List.builder().setTitle(Title.builder().setTitle("错误").setLevel(3).build())
                        .setItems(Lists.newArrayList(errors)).build().render()).toString();
    }
}
