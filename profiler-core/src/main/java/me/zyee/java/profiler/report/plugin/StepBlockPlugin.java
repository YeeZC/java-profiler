package me.zyee.java.profiler.report.plugin;

import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.flame.FlameParser;
import me.zyee.java.profiler.flame.Frame;
import me.zyee.java.profiler.report.markdown.Title;
import me.zyee.java.profiler.utils.LazyGet;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/2/2
 */
public class StepBlockPlugin implements Plugin {

    private final ProfileNode root;
    private final Set<String> warnings;
    private final Set<String> errors;
    private final LazyGet<Map<String, Frame>> frames;
    private final Map<String, Long> theoreticalCost;
    private final long actualCost;
    private long caseCost;
    private double profilerPercent;

    public static Builder builder(ProfileNode root, Set<String> warnings, Set<String> errors) {
        return new Builder(root, warnings, errors);
    }

    private StepBlockPlugin(Builder builder) {
        this.root = Objects.requireNonNull(builder.root, "root");
        this.warnings = Objects.requireNonNull(builder.warnings, "warnings");
        this.errors = Objects.requireNonNull(builder.errors, "errors");
        final Supplier<Map<String, Frame>> frames = Objects.requireNonNull(builder.frames, "frames");
        this.frames = new LazyGet.SupplierLazyGet<>(frames);
        this.theoreticalCost = Objects.requireNonNull(builder.theoreticalCost, "theoreticalCost");
        this.actualCost = Objects.requireNonNull(builder.cost, "cost");
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

        final StepBlock stepBlock = makeStepBlock(root, 0);
        StringJoiner joiner = new StringJoiner("\n\n");
        joiner.add(Title.builder().setTitle("详细步骤")
                .setLevel(2).build().render());
        final PriorityQueue<StepBlock> queue = stepBlock.getQueue();
        while (!queue.isEmpty()) {
            final StepBlock poll = queue.poll();
            joiner.add(renderStepBlock(poll, 2));
        }
        return joiner.toString();
    }

    private String renderStepBlock(StepBlock block, int parentLevel) {
        if (null == block.getTheoretical()) {

        }
        return "";
    }

    private double calculatePercent(double percent) {
        if (profilerPercent == 0) {
            return percent;
        }
        return 100 * percent / (100 - profilerPercent);
    }

    private StepBlock makeStepBlock(ProfileNode node, int deep) {
        final String pattern = node.getPattern();
        StepBlock.Builder builder = StepBlock.builder()
                .setDeep(deep).setTitle(node.getName())
                .setPattern(pattern);
        if (StringUtils.isNotEmpty(pattern)) {
            final Map<String, Frame> frames = this.frames.get();
            final Frame frame = frames.get(pattern);
            Optional.ofNullable(frame).map(Frame::getPercent)
                    .map(this::calculatePercent).ifPresent(builder::setPercent);
            final Long theoretical = theoreticalCost.get(pattern);
            Optional.ofNullable(theoretical).ifPresent(builder::setTheoretical);
        }
        final List<ProfileNode> children = node.getChildren();
        if (null != children && !children.isEmpty()) {
            children.forEach(child -> builder.addBlock(makeStepBlock(child, deep + 1)));

        }
        return builder.build();
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

        public StepBlockPlugin build() {
            return new StepBlockPlugin(this);
        }
    }
}
