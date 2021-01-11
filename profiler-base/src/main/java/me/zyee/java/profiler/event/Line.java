package me.zyee.java.profiler.event;


/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/11
 */
public class Line extends BaseEvent {

    private final int lineNumber;

    private Line(Builder builder) {
        super(builder.setType(Type.LINE));
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

        public Builder of(Line line) {
            this.lineNumber = line.lineNumber;
            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }

    @Override
    public String toString() {
        return "Line{" +
                "lineNumber=" + lineNumber +
                '}';
    }
}
