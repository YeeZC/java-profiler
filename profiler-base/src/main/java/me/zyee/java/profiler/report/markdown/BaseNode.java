package me.zyee.java.profiler.report.markdown;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public abstract class BaseNode implements Node {
    private final Title title;

    BaseNode(Builder<?> builder) {
        this.title = builder.title;
    }

    public Title getTitle() {
        return title;
    }

    protected static class Builder<T extends Builder<?>> {
        private Title title;

        public T setTitle(Title title) {
            this.title = title;
            return (T) this;
        }

        public T of(BaseNode baseNode) {
            this.title = baseNode.title;
            return (T) this;
        }
    }
}
