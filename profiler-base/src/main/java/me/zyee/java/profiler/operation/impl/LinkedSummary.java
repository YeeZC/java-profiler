package me.zyee.java.profiler.operation.impl;

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

    public void setOperation(BaseOperation operation) {
        this.operation = operation;
    }

    @Override
    public String render() {
        return super.render() + "[" + operation.getName() + "](#)";
    }
}
