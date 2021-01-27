package me.zyee.java.profiler.event;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/11
 */
public class CallThrows extends BaseEvent {
    private final int lineNumber;
    private Throwable throwable;

    private CallThrows(Builder builder) {
        super(builder.setType(Type.CALL_THROWS));
        this.lineNumber = builder.lineNumber;
        this.throwable = builder.throwable;
    }

    public static Builder builder() {
        return new Builder();
    }


    public int getLineNumber() {
        return lineNumber;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public void destroy() {
        throwable = null;
    }

    public static class Builder extends BaseEvent.Builder<Builder> {
        private Throwable throwable;
        private int lineNumber;

        private Builder() {
        }

        public Builder setThrowable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public Builder setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder of(CallThrows callBefore) {
            this.lineNumber = callBefore.lineNumber;
            return this;
        }

        public CallThrows build() {
            return new CallThrows(this);
        }
    }

    @Override
    public String toString() {
        return "CallThrows{" +
                "lineNumber=" + lineNumber +
                ", throwable=" + throwable +
                '}';
    }
}
