package me.zyee.java.profiler.report.plugin;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.flame.FlameParser;
import me.zyee.java.profiler.flame.Frame;
import me.zyee.java.profiler.utils.FormatUtil;
import me.zyee.java.profiler.utils.LazyGet;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/2/24
 */
public class StepHtmlPlugin implements HtmlPlugin {

    private final ProfileNode root;
    private final Set<String> warnings;
    private final Set<String> errors;
    private final LazyGet<Map<String, Frame>> frames;
    private final Map<String, Long> theoreticalCost;
    private final long actualCost;
    private long caseCost;
    private double profilerPercent;
    private final transient AtomicInteger sequence = new AtomicInteger(1000);


    private StepHtmlPlugin(Builder builder) {
        this.root = Objects.requireNonNull(builder.root, "root");
        this.warnings = Objects.requireNonNull(builder.warnings, "warnings");
        this.errors = Objects.requireNonNull(builder.errors, "errors");
        final Supplier<Map<String, Frame>> frames = Objects.requireNonNull(builder.frames, "frames");
        this.frames = new LazyGet.SupplierLazyGet<>(frames);
        this.theoreticalCost = Objects.requireNonNull(builder.theoreticalCost, "theoreticalCost");
        this.actualCost = Objects.requireNonNull(builder.cost, "cost");
    }

    public static Builder builder(ProfileNode node, Set<String> warnings, Set<String> errors) {
        return new Builder(node, warnings, errors);
    }

    @Override
    @JsonProperty
    public String getTitle() {
        return "详细步骤";
    }

    @Override
    @JsonProperty
    public List<Object> getData() {
        final Frame frame = frames.get().get(FlameParser.SELF_PATTERN);
        if (null == frame) {
            caseCost = actualCost;
        } else {
            profilerPercent = frame.getPercent();
            caseCost = (long) (actualCost * (100 - profilerPercent) / 100);
        }
        caseCost = actualCost;
        final List<Map<String, Object>> expandData = (List<Map<String, Object>>) makeRows(root).get(0).get("expandData");

        return expandData
                .stream().filter(map -> map.containsKey("percent"))
                .peek(this::fill)
                .peek(this::format)
                .collect(Collectors.toList());
    }

    private void format(Map<String, Object> data) {
        Optional.ofNullable(data.get("theoretical")).ifPresent(t -> {
            long theoretical = ((Number) t).longValue();
            data.put("theoretical", FormatUtil.formatMilliseconds(theoretical));
        });
        Optional.ofNullable(data.get("cost")).ifPresent(t -> {
            long cost = ((Number) t).longValue();
            data.put("cost", FormatUtil.formatMilliseconds(cost));
        });
        Optional.ofNullable(data.get("totalCost")).ifPresent(t -> {
            long totalCost = ((Number) t).longValue();
            data.put("totalCost", FormatUtil.formatMilliseconds(totalCost));
        });
        Optional.ofNullable(data.get("percent")).ifPresent(t -> {
            double percent = ((Number) t).longValue();
            data.put("percent", String.format("%.2f%%", percent));
        });
        final List<Map<String, Object>> expandData = (List<Map<String, Object>>) data.get("expandData");
        expandData.forEach(this::format);
    }

    private void fill(Map<String, Object> expandData) {
        final List<Map<String, Object>> data = (List<Map<String, Object>>) expandData.get("expandData");
        if (!data.isEmpty()) {
            data.forEach(this::fill);
            if (!expandData.containsKey("theoretical") || (long) expandData.get("theoretical") == 0L) {
                data.stream().map(map -> map.get("theoretical"))
                        .filter(Objects::nonNull).map(Long.class::cast).reduce(Long::sum)
                        .ifPresent(sum -> expandData.put("theoretical", sum));
            }
        }
        handleWarning(expandData);
        expandData.put("totalCost", caseCost);
    }

    private void handleWarning(Map<String, Object> data) {
        handleRowWarning(data);
        final double totalPercent = root.getChildren().stream().map(ProfileNode::getPattern)
                .mapToDouble(pattern -> {
                    final Frame frame = frames.get().get(pattern);
                    return Optional.ofNullable(frame).map(Frame::getPercent).map(this::calculatePercent).orElse(0D);
                }).sum();
        if (totalPercent < 80) {
            errors.add(Warning.eighty_percent.warning);
        }
    }

    private void handleRowWarning(Map<String, Object> data) {
        String format = "%s%s";
        final Set<String> warning = (Set<String>) data.get("warning");
        final Object name = data.get("name");
        if (!data.containsKey("theoretical")) {
            warning.add(Warning.theoretical.warning);
            this.warnings.add(String.format(format, name, Warning.theoretical.warning));
        } else {
            Optional.ofNullable(data.get("cost")).ifPresent(cost -> {
                double actual = (double) cost;
                long theoretical = (long) data.get("theoretical");
                if (actual / theoretical > 10) {
                    warning.add(Warning.magnitude.warning);
                    errors.add(String.format(format, name, Warning.magnitude.warning));
                } else if (actual / theoretical > 2) {
                    warning.add(Warning.two.warning);
                    errors.add(String.format(format, name, Warning.two.warning));
                } else if (actual / theoretical > 1.5) {
                    warning.add(Warning.one_half.warning);
                    errors.add(String.format(format, name, Warning.one_half.warning));
                }
            });
        }
    }

    @Override
    @JsonProperty
    public List<HtmlTableColumn> getColumns() {
        return Lists.newArrayList(
                HtmlTableColumn.builder().setTitle("操作名称").setKey("name").build(),
                HtmlTableColumn.builder().setTitle("表达式").setKey("pattern").build(),
                HtmlTableColumn.builder().setTitle("理论耗时").setKey("theoretical").build(),
                HtmlTableColumn.builder().setTitle("耗时").setKey("cost").build(),
                HtmlTableColumn.builder().setTitle("百分比").setKey("percent").build(),
                HtmlTableColumn.builder().setTitle("总耗时").setKey("totalCost").build(),
                HtmlTableColumn.builder().setTitle("Warning").setKey("warning").build()

        );
    }

    private List<Map<String, Object>> makeRows(ProfileNode node) {
        List<Map<String, Object>> data = new ArrayList<>();
        String stepName = node.getName();
        final Map<String, Object> row = new HashMap<>();
        row.put("expandData", new ArrayList<>());
        row.put("warning", new HashSet<>());
        row.put("id", sequence.getAndIncrement());
        data.add(row);
        if (StringUtils.isNotEmpty(node.getSummery())) {
            row.put("summary", node.getSummery());
        }
        final List<ProfileNode> expandData = node.getChildren();
        if (null == expandData || expandData.isEmpty()) {

            final String pattern = node.getPattern();
            if (StringUtils.isNotEmpty(pattern)) {
                final Frame frame = frames.get().get(pattern);
                if (null != frame) {
                    final double percent = calculatePercent(frame.getPercent());
                    row.put("percent", percent);
                    row.put("cost", caseCost * percent / 100);
                }
                final Long theoretical = theoreticalCost.get(pattern);
                if (null != theoretical) {
                    row.put("theoretical", theoretical);
                }
            }
            row.put("name", stepName);
            row.put("pattern", pattern);

        } else {
            for (ProfileNode child : expandData) {
                ((List<Object>) row.get("expandData")).addAll(makeRows(child));
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
                row.put("name", stepName);
                row.put("pattern", pattern);
                row.put("theoretical", data.stream()
                        .mapToLong(datum ->
                                Optional.ofNullable(datum.get("theoretical"))
                                        .map(Long.class::cast).orElse(0L)).sum());
                row.put("percent", percent);
                row.put("cost", caseCost * percent / 100);
            }
        }
        return data;
    }

    private double calculatePercent(double percent) {
        if (profilerPercent == 0) {
            return percent;
        }
        return 100 * percent / (100 - profilerPercent);
    }

    @Override
    @JsonProperty
    public boolean isExpandable() {
        return true;
    }

    @Override
    @JsonProperty
    public boolean isHint() {
        return true;
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

        public StepHtmlPlugin build() {
            return new StepHtmlPlugin(this);
        }
    }
}
