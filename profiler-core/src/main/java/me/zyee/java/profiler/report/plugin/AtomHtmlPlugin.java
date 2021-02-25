package me.zyee.java.profiler.report.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.utils.FormatUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/2/24
 */
public class AtomHtmlPlugin implements HtmlPlugin {
    private final transient ProfileNode root;

    public AtomHtmlPlugin(ProfileNode root) {
        this.root = root;
    }

    @Override
    @JsonProperty
    public String getTitle() {
        return "原子操作";
    }

    @Override
    @JsonProperty
    public List<Object> getData() {
        return new ArrayList<>(findAtom(root));
    }

    @Override
    @JsonProperty
    public List<HtmlTableColumn> getColumns() {
        return Lists.newArrayList(
                HtmlTableColumn.builder().setTitle("操作名称").setKey("name").build(),
                HtmlTableColumn.builder().setTitle("表达式").setKey("pattern").build(),
                HtmlTableColumn.builder().setTitle("理论耗时 (kw次)").setKey("cost").build(),
                HtmlTableColumn.builder().setTitle("备注").setKey("summary").build()
        );
    }

    @Override
    @JsonProperty
    public boolean isExpandable() {
        return false;
    }

    private List<Map<String, String>> findAtom(ProfileNode node) {
        List<Map<String, String>> data = new ArrayList<>();
        if (null != node.getAtom()) {
            final String pattern = node.getPattern();
            Map<String, String> item = new HashMap<>(4);
            item.put("name", node.getName());
            item.put("pattern", StringUtils.isEmpty(pattern) ? "" : pattern);
            item.put("cost", FormatUtil.formatMilliseconds((node.getAtom()).longValue()));
            item.put("summary", Optional.ofNullable(node.getSummery()).orElse(""));
            data.add(item);
        } else if (null != node.getChildren()) {
            for (ProfileNode child : node.getChildren()) {
                data.addAll(findAtom(child));
            }
        }
        return data;
    }
}
