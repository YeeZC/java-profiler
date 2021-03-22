package me.zyee.java.profiler.report.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        List<AtomBean> data = new ArrayList<>();
        findAtom(root, data);
        return new ArrayList<>(data);
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

    private void findAtom(ProfileNode node, List<AtomBean> data) {
        if (null != node.getAtom()) {
            final String pattern = node.getPattern();
            AtomBean item = new AtomBean();
            item.name = node.getName();
            item.pattern = StringUtils.isEmpty(pattern) ? "" : pattern;
            item.cost = FormatUtil.formatMilliseconds((node.getAtom()).longValue());
            item.summary = Optional.ofNullable(node.getSummery()).orElse("");
            if (!data.contains(item)) {
                data.add(item);
            }
        } else if (null != node.getChildren()) {
            for (ProfileNode child : node.getChildren()) {
                findAtom(child, data);
            }
        }
    }

    private static class AtomBean {
        private String name;
        private String pattern;
        private String cost;
        private String summary;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AtomBean atomBean = (AtomBean) o;
            return Objects.equals(name, atomBean.name) && Objects.equals(pattern, atomBean.pattern);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, pattern);
        }

        public String getName() {
            return name;
        }

        public String getPattern() {
            return pattern;
        }

        public String getCost() {
            return cost;
        }

        public String getSummary() {
            return summary;
        }
    }
}
