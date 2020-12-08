package me.zyee.java.profiler;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public interface Operation {
    /**
     * Operation Name
     *
     * @return
     */
    String getName();

    /**
     * Operation Cost
     *
     * @return
     */
    long getCost();

    /**
     * Operation Pattern
     *
     * @return
     */
    String getPattern();
}
