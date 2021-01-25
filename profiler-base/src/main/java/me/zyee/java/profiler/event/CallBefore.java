package me.zyee.java.profiler.event;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/11
 */
public class CallBefore extends BaseEvent {
    private final Before before;
    private final String triggerClass;
    private final String triggerMethod;
    private final String triggerMethodSign;
    private final int lineNumber;

    private CallBefore(Builder builder) {
        super(builder.setType(Type.CALL_BEFORE));
        this.triggerClass = builder.triggerClass;
        this.triggerMethod = builder.triggerMethod;
        this.triggerMethodSign = builder.triggerMethodSign;
        this.lineNumber = builder.lineNumber;
        this.before = builder.before;
    }

    public static Builder builder() {
        return new Builder();
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

    public int getLineNumber() {
        return lineNumber;
    }

    public static class Builder extends BaseEvent.Builder<Builder> {
        private Before before;
        private String triggerClass;
        private String triggerMethod;
        private String triggerMethodSign;
        private int lineNumber;

        private Builder() {
        }

        public Builder setTriggerClass(String triggerClass) {
            this.triggerClass = triggerClass;
            return this;
        }

        public Builder setTriggerMethod(String triggerMethod) {
            this.triggerMethod = triggerMethod;
            return this;
        }

        public Builder setTriggerMethodSign(String triggerMethodSign) {
            this.triggerMethodSign = triggerMethodSign;
            return this;
        }

        public Builder setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder setBefore(Before before) {
            this.before = before;
            return this;
        }

        public Builder of(CallBefore callBefore) {
            this.triggerClass = callBefore.triggerClass;
            this.triggerMethod = callBefore.triggerMethod;
            this.triggerMethodSign = callBefore.triggerMethodSign;
            this.lineNumber = callBefore.lineNumber;
            this.before = callBefore.before;
            return this;
        }

        public CallBefore build() {
            return new CallBefore(this);
        }
    }

    @Override
    public String toString() {
        return "CallBefore{" +
                "triggerClass='" + triggerClass + '\'' +
                ", triggerMethod='" + triggerMethod + '\'' +
                ", triggerMethodSign='" + triggerMethodSign + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
