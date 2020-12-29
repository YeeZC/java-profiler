package me.zyee.java.profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/8/24
 */
public class ProfileNode {
    private String pattern;
    private String name;
    private Double atom;
    private List<ProfileNode> children;
    private ProfileNode parent;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProfileNode> getChildren() {
        return children;
    }

    public void setChildren(List<ProfileNode> children) {
        this.children = children.stream().peek(node -> node.parent = this).collect(Collectors.toList());
    }

    public void addChild(ProfileNode child) {
        if (null == children) {
            children = new ArrayList<>();
        }
        this.children.add(child);
        child.parent = this;
    }

    public ProfileNode getParent() {
        return parent;
    }

    public void setParent(ProfileNode parent) {
        this.parent = parent;
    }

    public Double getAtom() {
        return atom;
    }

    public void setAtom(Double atom) {
        this.atom = atom;
    }

    public void merge() {
        if (null != this.children) {
            this.children = this.children.stream().distinct().
                    peek(ProfileNode::merge).collect(Collectors.toList());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfileNode that = (ProfileNode) o;
        return Objects.equals(pattern, that.pattern) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, name);
    }
}
