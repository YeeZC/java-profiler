package me.zyee.java.profiler.report.plugin;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/2/2
 */
public class StepBlock implements Comparable<StepBlock> {
    private final String title;
    private final String pattern;
    private final int deep;
    private final double percent;
    private final Long theoretical;
    private final PriorityQueue<StepBlock> queue;

    private StepBlock(Builder builder) {
        this.title = builder.title;
        this.pattern = builder.pattern;
        this.deep = builder.deep;
        this.percent = builder.percent;
        this.theoretical = builder.theoretical;
        this.queue = builder.queue;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int compareTo(StepBlock o) {
        return Double.compare(this.percent, o.percent);
    }

    public String getTitle() {
        return title;
    }

    public String getPattern() {
        return pattern;
    }

    public int getDeep() {
        return deep;
    }

    public double getPercent() {
        return percent;
    }

    public Long getTheoretical() {
        return theoretical;
    }

    public PriorityQueue<StepBlock> getQueue() {
        return queue;
    }

    public static class Builder {
        private String title;
        private String pattern;
        private int deep;
        private double percent;
        private double stepPercent;
        private Long theoretical;
        private PriorityQueue<StepBlock> queue = new PriorityQueue<>(Comparator.reverseOrder());

        private Builder() {
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setPattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder setDeep(int deep) {
            this.deep = deep;
            return this;
        }

        public Builder setPercent(double percent) {
            this.percent = percent;
            return this;
        }

        public Builder setStepPercent(double stepPercent) {
            this.stepPercent = stepPercent;
            return this;
        }

        public Builder setTheoretical(Long theoretical) {
            this.theoretical = theoretical;
            return this;
        }

        public Builder addBlock(StepBlock block) {
            this.queue.add(block);
            return this;
        }

        public Builder of(StepBlock stepBlock) {
            this.title = stepBlock.title;
            this.pattern = stepBlock.pattern;
            this.deep = stepBlock.deep;
            this.percent = stepBlock.percent;
            this.theoretical = stepBlock.theoretical;
            this.queue = stepBlock.queue;
            return this;
        }

        public StepBlock build() {
            return new StepBlock(this);
        }
    }
}
