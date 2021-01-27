package me.zyee.java.profiler.report.plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.flame.FlameParser;
import me.zyee.java.profiler.flame.Frame;
import me.zyee.java.profiler.report.markdown.Table;
import me.zyee.java.profiler.report.markdown.Title;
import me.zyee.java.profiler.utils.FormatUtil;
import me.zyee.java.profiler.utils.LazyGet;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class StepPlugin implements Plugin {
    private final ProfileNode root;
    private final Set<String> warnings;
    private final Set<String> errors;
    private final LazyGet<Map<String, Frame>> frames;
    private final Map<String, Long> theoreticalCost;
    private final long actualCost;
    private long caseCost;
    private double profilerPercent;

    private StepPlugin(Builder builder) {
        this.root = Objects.requireNonNull(builder.root, "root");
        this.warnings = Objects.requireNonNull(builder.warnings, "warnings");
        this.errors = Objects.requireNonNull(builder.errors, "errors");
        final Supplier<Map<String, Frame>> frames = Objects.requireNonNull(builder.frames, "frames");
        this.frames = new LazyGet.SupplierLazyGet<>(frames);
        this.theoreticalCost = Objects.requireNonNull(builder.theoreticalCost, "theoreticalCost");
        this.actualCost = Objects.requireNonNull(builder.cost, "cost");
    }

    public static Builder builder(ProfileNode root, Set<String> warnings, Set<String> errors) {
        return new Builder(root, warnings, errors);
    }

    @Override
    public String render() {
        final Frame frame = frames.get().get(FlameParser.SELF_PATTERN);
        if (null == frame) {
            caseCost = actualCost;
        } else {
            profilerPercent = frame.getPercent();
            caseCost = (long) (actualCost * (100 - profilerPercent) / 100);
        }
        caseCost = actualCost;
        final Table.Builder builder = Table.builder().setTitle(Title.builder().setTitle("详细步骤").setLevel(2).build())
                .setHeader("步骤名称", "表达式", "原子操作", "原子表达式", "理论耗时", "实际耗时", "步骤百分比", "总百分比", "总耗时", "警告");
        makeRowData(root, root.getName()).stream()
                .filter(data -> data.totalPercent > 0)
                .peek(this::handleWarning)
                .map(StepRowData::toRow).forEach(builder::addRow);
        return builder.build().render();
    }

    private double calculatePercent(double percent) {
        if (profilerPercent == 0) {
            return percent;
        }
        return 100 * percent / (100 - profilerPercent);
    }

    private void handleWarning(StepRowData data) {
        String format = "%s%s";
        if (data.theoretical == 0) {
            data.warning.add(Warning.theoretical);
            warnings.add(String.format(format, data.name, Warning.theoretical.warning));
        } else {
            final double actual = data.getActual();
            if (actual / data.theoretical > 10) {
                data.warning.add(Warning.magnitude);
                errors.add(String.format(format, data.name, Warning.magnitude.warning));
            } else if (actual / data.theoretical > 2) {
                data.warning.add(Warning.two);
                errors.add(String.format(format, data.name, Warning.two.warning));
            } else if (actual / data.theoretical > 1.5) {
                data.warning.add(Warning.one_half);
                errors.add(String.format(format, data.name, Warning.one_half.warning));
            }
        }
        final double totalPercent = root.getChildren().stream().map(ProfileNode::getPattern)
                .mapToDouble(pattern -> {
                    final Frame frame = frames.get().get(pattern);
                    return Optional.ofNullable(frame).map(Frame::getPercent).map(this::calculatePercent).orElse(0D);
                }).sum();
        if (totalPercent < 80) {
            errors.add(Warning.eighty_percent.warning);
        }
    }

    public static class Builder {
        private final ProfileNode root;
        private final Set<String> warnings;
        private final Set<String> errors;

        private Supplier<Map<String, Frame>> frames;
        private Map<String, Long> theoreticalCost;
        private Long cost;

        private Builder(ProfileNode root, Set<String> warnings, Set<String> errors) {
            this.root = root;
            this.warnings = warnings;
            this.errors = errors;
        }

        public Builder setFrames(Supplier<Map<String, Frame>> frames) {
            this.frames = frames;
            return this;
        }

        public Builder setTheoreticalCost(Map<String, Long> theoreticalCost) {
            this.theoreticalCost = theoreticalCost;
            return this;
        }

        public Builder setCost(long cost) {
            this.cost = cost;
            return this;
        }

        public StepPlugin build() {
            return new StepPlugin(this);
        }
    }

    private List<StepRowData> makeRowData(ProfileNode node, String name) {
        List<StepRowData> data = new ArrayList<>();
        String stepName = StringUtils.isEmpty(name) ? node.getName() : name + "->" + node.getName();
        final List<ProfileNode> children = node.getChildren();
        if (null == children || children.isEmpty()) {
            final StepRowData row = new StepRowData();
            final String pattern = node.getPattern();
            if (StringUtils.isNotEmpty(pattern)) {
                final Frame frame = frames.get().get(pattern);
                if (null != frame) {
                    row.totalPercent = calculatePercent(frame.getPercent());
                }
                final Long theoretical = theoreticalCost.get(pattern);
                if (null != theoretical) {
                    row.theoretical = theoretical;
                    row.atomName = node.getName();
                    row.atomPattern = pattern;
                }
            }
            row.name = stepName;
            row.pattern = pattern;
            row.stepPercent = 0D;
            data.add(row);
        } else {
            for (ProfileNode child : children) {
                data.addAll(makeRowData(child, stepName));
            }
            double percent = 0D;
            final String pattern = node.getPattern();
            if (StringUtils.isNotEmpty(pattern)) {
                final Frame frame = frames.get().get(pattern);
                if (null != frame) {
                    percent = calculatePercent(frame.getPercent());
                }
            }

            if (!node.equals(root)) {
                final double stepCost = caseCost * percent / 100;
                final StepRowData row = new StepRowData();
                row.name = stepName;
                row.pattern = pattern;
                row.theoretical = data.stream().peek(datum -> datum.stepPercent = datum.getActual() * 100 / stepCost)
                        .mapToLong(datum -> datum.theoretical).sum();
                row.stepPercent = 100D;
                row.totalPercent = percent;
                data.add(row);
            }
        }
        return data;
    }

    private class StepRowData {
        private String name;
        private String pattern;
        private String atomName;
        private String atomPattern;
        private long theoretical;
        private double stepPercent;
        private double totalPercent;
        private final Set<Warning> warning = new HashSet<>(0);

        String[] toRow() {
            return new String[]{
                    Optional.ofNullable(name).orElse(""),
                    StringUtils.isEmpty(pattern) ? "" : String.format("`%s`", pattern),
                    Optional.ofNullable(atomName).orElse(""),
                    StringUtils.isEmpty(atomPattern) ? "" : String.format("`%s`", atomPattern),
                    FormatUtil.formatMilliseconds(theoretical),
                    FormatUtil.formatMilliseconds((long) (caseCost * totalPercent / 100)),
                    String.format("%.2f%%", stepPercent),
                    String.format("%.2f%%", totalPercent),
                    FormatUtil.formatMilliseconds(caseCost),
                    warning.stream().map(war -> war.warning).collect(Collectors.joining(","))
            };
        }

        double getActual() {
            return (caseCost * totalPercent / 100);
        }
    }
}
