package me.zyee.java.profiler.report.plugin;

import com.google.common.collect.ImmutableList;
import me.zyee.java.profiler.ProfileNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/3/5
 */
public class SummaryHtmlPlugin implements HtmlPlugin {
    private final ProfileNode root;

    public SummaryHtmlPlugin(ProfileNode root) {
        this.root = root;
    }

    @Override
    public String getTitle() {
        return "算法描述";
    }

    @Override
    public List<Object> getData() {
        return makeData(root, 0);
    }

    private List<Object> makeData(ProfileNode node, int deep) {
        final ImmutableList.Builder<Object> builder = ImmutableList.builder();
        for (ProfileNode child : node.getChildren()) {
            if (StringUtils.isNotEmpty(child.getSummery()) && null == child.getAtom()) {
                final String[] split = child.getSummery().split("\n");
                for (String s : split) {
                    StringBuilder str = new StringBuilder();
                    for (int i = 0; i < deep; i++) {
                        str.append("\t");
                    }
                    str.append(s);
                    builder.add(str.toString());
                }
                builder.addAll(makeData(child, deep + 1));
            }
        }
        return builder.build();
    }

    @Override
    public List<HtmlTableColumn> getColumns() {
        return null;
    }

    @Override
    public boolean isExpandable() {
        return false;
    }

    @Override
    public PluginType getType() {
        return PluginType.md;
    }

}
