package me.zyee.java.profiler.report.plugin;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.report.markdown.Table;
import me.zyee.java.profiler.report.markdown.Title;
import me.zyee.java.profiler.utils.FormatUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class AtomPlugin implements Plugin {

    private final ProfileNode root;

    public AtomPlugin(ProfileNode root) {
        this.root = root;
        System.err.println("Node------ " + root);
    }

    @Override
    public String render() {
        final Table.Builder builder = Table.builder().setTitle(Title.builder().setTitle("原子操作").setLevel(2).build())
                .setHeader("操作名称", "表达式", "理论耗时 (kw次)", "备注");
        findAtom(root).forEach(builder::addRow);
        return builder.build().render();
    }

    private List<List<String>> findAtom(ProfileNode node) {
        List<List<String>> data = new ArrayList<>();
        if (null != node.getAtom()) {
            final String pattern = node.getPattern();
            data.add(Lists.newArrayList(
                    node.getName(),
                    StringUtils.isEmpty(pattern) ? "" : String.format("`%s`", pattern),
                    FormatUtil.formatMilliseconds((node.getAtom()).longValue()), ""
            ));
        } else {
            for (ProfileNode child : node.getChildren()) {
                data.addAll(findAtom(child));
            }
        }
        return data;
    }

}
