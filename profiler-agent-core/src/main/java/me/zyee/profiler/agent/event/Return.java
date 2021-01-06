package me.zyee.profiler.agent.event;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class Return extends BaseEvent {
    private final Object returnObject;


    private Return(Builder builder) {
        super(builder.setType(Type.RETURN));
        this.returnObject = builder.returnObject;
    }

    public static Builder builder() {
        return new Builder();
    }


    public Object getReturnObject() {
        return returnObject;
    }

    public static class Builder extends BaseEvent.Builder<Builder> {
        private Object returnObject;

        public Builder setReturnObject(Object returnObject) {
            this.returnObject = returnObject;
            return this;
        }

        public Builder of(Return obj) {
            super.of(obj);
            this.returnObject = obj.returnObject;
            return this;
        }

        public Return build() {
            return new Return(this);
        }
    }
}
