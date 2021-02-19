package me.zyee.java.profiler.event;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class Throws extends BaseEvent {
    private Throwable throwable;

    private Throws(Builder builder) {
        super(builder.setType(Type.THROWS));
        this.throwable = builder.throwable;
    }

    public static Builder builder() {
        return new Builder();
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

        public Builder setThrowable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public Builder of(Throws obj) {
            super.of(obj);
            this.throwable = obj.throwable;
            return this;
        }

        public Throws build() {
            return new Throws(this);
        }
    }

    @Override
    public String toString() {
        return "Throws{" +
                "throwable=" + throwable +
                '}';
    }
}
