package me.zyee.java.profiler.event;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/11
 */
public class CallReturn extends BaseEvent {
    private final int lineNumber;

    private CallReturn(Builder builder) {
        super(builder.setType(Type.CALL_RETURN));
        this.lineNumber = builder.lineNumber;
    }

    public static Builder builder() {
        return new Builder();
    }


    public int getLineNumber() {
        return lineNumber;
    }


    public static class Builder extends BaseEvent.Builder<Builder> {
        private int lineNumber;

        private Builder() {
        }

        public Builder setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder of(CallReturn callBefore) {
            this.lineNumber = callBefore.lineNumber;
            return this;
        }

        public CallReturn build() {
            return new CallReturn(this);
        }
    }

    @Override
    public String toString() {
        return "CallReturn{" +
                "lineNumber=" + lineNumber +
                '}';
    }
}
