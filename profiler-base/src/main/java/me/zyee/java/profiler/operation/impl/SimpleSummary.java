package me.zyee.java.profiler.operation.impl;

import me.zyee.java.profiler.operation.Summary;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/3/14
 */
public class SimpleSummary implements Summary {
    private final String summary;

    public SimpleSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String render() {
        return null;
    }
}
