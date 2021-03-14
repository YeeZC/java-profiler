package me.zyee.java.profiler.operation.impl;

import me.zyee.java.profiler.operation.Summary;
import me.zyee.java.profiler.utils.StringHelper;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/3/14
 */
public class LinkedSummary extends SimpleSummary {
    private BaseOperation operation;


    public LinkedSummary(String summary) {
        super(summary);
    }

    public LinkedSummary() {
    }

    public void setOperation(BaseOperation operation) {
        this.operation = operation;
    }

    @Override
    public String render() {
        return super.render() + "[" + operation.getName() + "](#)";
    }

    @Override
    public Summary valueOf(String valueOf) {
        return StringHelper.fromArgs(valueOf, new LinkedSummary());
    }
}
