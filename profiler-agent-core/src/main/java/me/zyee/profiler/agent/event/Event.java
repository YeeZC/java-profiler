package me.zyee.profiler.agent.event;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public interface Event {
    Type type();

    enum Type {
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
        THROWS
    }
}
