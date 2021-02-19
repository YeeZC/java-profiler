package me.zyee.java.profiler.report.markdown;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class Title implements Node {
    private final String title;
    private final int level;

    private Title(Builder builder) {
        this.title = builder.title;
        this.level = Math.min(Math.max(1, builder.level), 6);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String render() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append("#");
        }
        builder.append(" ").append(title).append("\n");
        return builder.toString();
    }


    public String getTitle() {
        return title;
    }

    public int getLevel() {
        return level;
    }

    public static class Builder {
        private String title;
        private int level;

        private Builder() {
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setLevel(int level) {
            this.level = Math.min(Math.max(1, level), 6);
            return this;
        }

        public Builder of(Title title) {
            this.title = title.title;
            this.level = title.level;
            return this;
        }

        public Title build() {
            return new Title(this);
        }
    }
}
