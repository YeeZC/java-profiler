package me.zyee.java.profiler.report.plugin;

import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.report.markdown.Title;
import org.apache.commons.lang3.StringUtils;

import java.util.StringJoiner;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/2/22
 */
public class SummaryPlugin implements Plugin {
    private final ProfileNode root;

    private SummaryPlugin(Builder builder) {
        this.root = builder.root;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String render() {
        StringJoiner joiner = new StringJoiner("\n");
        for (ProfileNode child : root.getChildren()) {
            final String summery = child.getSummery();
            if (StringUtils.isNotEmpty(summery)) {
                joiner.add(Title.builder().setLevel(3).setTitle(child.getName()).build().render())
                        .add(summery);
            }
        }
        if (StringUtils.isNotEmpty(joiner.toString())) {
            final String describe = Title.builder().setLevel(2).setTitle("步骤描述")
                    .build().render();
            return describe + "\n" + joiner;
        }
        return "";
    }


    public static class Builder {
        private ProfileNode root;

        private Builder() {
        }

        public Builder setRoot(ProfileNode root) {
            this.root = root;
            return this;
        }

        public Builder of(SummaryPlugin summaryPlugin) {
            this.root = summaryPlugin.root;
            return this;
        }

        public SummaryPlugin build() {
            return new SummaryPlugin(this);
        }
    }
}
