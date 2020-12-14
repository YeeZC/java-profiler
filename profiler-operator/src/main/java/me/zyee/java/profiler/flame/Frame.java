package me.zyee.java.profiler.flame;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/8/17
 */
public class Frame {
    private Frame parent;
    private double percent;
    private String name;
    private long count;
    private long selfCount;
    private double selfPercent;

    public Frame getParent() {
        return parent;
    }

    public void setParent(Frame parent) {
        this.parent = parent;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getSelfCount() {
        return selfCount;
    }

    public void setSelfCount(long selfCount) {
        this.selfCount = selfCount;
    }

    public double getSelfPercent() {
        return selfPercent;
    }

    public void setSelfPercent(double selfPercent) {
        this.selfPercent = selfPercent;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "parent=" + parent +
                ", percent=" + percent +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", selfCount=" + selfCount +
                ", selfPercent=" + selfPercent +
                '}';
    }
}
