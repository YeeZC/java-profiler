package me.zyee.java.profiler.event;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public interface Event {
    Type type();

    Event Entry = () -> Type.ENTRY;

    default void destroy() {
    }

    enum Type {
        /**
         * Entry
         */
        ENTRY,
        /**
         * 调用:BEFORE
         */
        BEFORE,

        /**
         * 调用:RETURN
         */
        RETURN,

        /**
         * 调用:THROWS
         */
        THROWS,

        CALL_BEFORE,

        CALL_RETURN,

        CALL_THROWS,

        LINE
    }
}
