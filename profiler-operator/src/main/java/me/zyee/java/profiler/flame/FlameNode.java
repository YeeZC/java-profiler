package me.zyee.java.profiler.flame;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/8/25
 */
public class FlameNode {
    private FlameNode parent;
    private double percent;
    private String name;
    private long count;
    private long selfCount;
    private double selfPercent;
    private List<FlameNode> children = new ArrayList<>();

    public FlameNode getParent() {
        return parent;
    }

    public void setParent(FlameNode parent) {
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

    public List<FlameNode> getChildren() {
        return children;
    }

    public void setChildren(List<FlameNode> children) {
        this.children = children;
    }

    public void add(FlameNode node) {
        children.add(node);
        node.setParent(this);
    }
}
