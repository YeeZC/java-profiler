package me.zyee.java.profiler.event;

import me.zyee.java.profiler.event.annotation.AutoClear;

import java.util.Arrays;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class Before extends BaseEvent {
    private final ClassLoader triggerLoader;
    private final String triggerClass;
    private final String triggerMethod;
    private final String triggerMethodSign;
    private Object trigger;
    private Object[] args;

    private Before(Builder builder) {
        super(builder.setType(Type.BEFORE));
        this.triggerLoader = builder.triggerLoader;
        this.triggerClass = builder.triggerClass;
        this.triggerMethod = builder.triggerMethod;
        this.trigger = builder.trigger;
        this.triggerMethodSign = builder.triggerMethodSign;
        this.args = builder.args;
    }

    public static Builder builder() {
        return new Builder();
    }


    public ClassLoader getTriggerLoader() {
        return triggerLoader;
    }

    public String getTriggerClass() {
        return triggerClass;
    }

    public String getTriggerMethod() {
        return triggerMethod;
    }

    public String getTriggerMethodSign() {
        return triggerMethodSign;
    }

    public Object getTrigger() {
        return trigger;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public void destroy() {
        trigger = null;
        args = null;
    }

    public static class Builder extends BaseEvent.Builder<Builder> {
        private ClassLoader triggerLoader;
        private String triggerClass;
        private String triggerMethod;
        private String triggerMethodSign;
        private Object trigger;
        private Object[] args;

        private Builder() {
        }

        public Builder setTriggerLoader(ClassLoader triggerLoader) {
            this.triggerLoader = triggerLoader;
            return this;
        }

        public Builder setTriggerClass(String triggerClass) {
            this.triggerClass = triggerClass;
            return this;
        }

        public Builder setTriggerMethod(String triggerMethod) {
            this.triggerMethod = triggerMethod;
            return this;
        }

        public Builder setTrigger(Object trigger) {
            this.trigger = trigger;
            return this;
        }

        public Builder setArgs(Object[] args) {
            this.args = args;
            return this;
        }

        public Builder setTriggerMethodSign(String triggerMethodSign) {
            this.triggerMethodSign = triggerMethodSign;
            return this;
        }

        public Builder of(Before before) {
            super.of(before);
            this.triggerLoader = before.triggerLoader;
            this.triggerClass = before.triggerClass;
            this.triggerMethod = before.triggerMethod;
            this.trigger = before.trigger;
            this.args = before.args;
            this.triggerMethodSign = before.triggerMethodSign;
            return this;
        }

        public Before build() {
            return new Before(this);
        }
    }

    @Override
    public String toString() {
        return "Before{" +
                "triggerLoader=" + triggerLoader +
                ", triggerClass='" + triggerClass + '\'' +
                ", triggerMethod='" + triggerMethod + '\'' +
                ", trigger=" + trigger +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
