package me.zyee.java.profiler;

/**
 * 生命周期
 *
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public interface LifeCycle {
    /**
     * Profiler之前的处理
     *
     * @param task
     * @return
     */
    LifeCycle onBefore(Task task);

    /**
     * Profiler之后的数据处理
     *
     * @param task
     * @return
     */
    LifeCycle onAfter(Task task);

    /**
     * 失败后的处理
     *
     * @param task
     * @return
     */
    LifeCycle onFailed(Task task);

    /**
     * 结束后的处理
     *
     * @param task
     * @return
     */
    LifeCycle onFinished(Task task);
}
