package me.zyee.java.profiler.report.plugin;

import com.google.common.collect.ImmutableList;
import java.util.List;
import me.zyee.java.profiler.ProfileNode;
import org.apache.commons.lang3.StringUtils;

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
        final ImmutableList.Builder<Object> builder = ImmutableList.builder();
        for (ProfileNode child : root.getChildren()) {
            if (StringUtils.isNotEmpty(child.getSummery()) && null == child.getAtom()) {
                builder.add(child.getSummery());
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
