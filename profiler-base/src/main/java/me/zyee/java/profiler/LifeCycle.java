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
     * 数据准备
     *
     * @param listener
     * @return
     */
    LifeCycle onPrepare(ProfilerListener listener);

    /**
     * Profiler之前的处理
     *
     * @param listener
     * @return
     */
    LifeCycle onBefore(ProfilerListener listener);

    /**
     * Profiler之后的数据处理
     *
     * @param listener
     * @return
     */
    LifeCycle onAfter(ProfilerListener listener);

    /**
     * Profiler后清理数据
     *
     * @param listener
     * @return
     */
    LifeCycle onClean(ProfilerListener listener);

    /**
     * 失败后的处理
     *
     * @param listener
     * @return
     */
    LifeCycle onFailed(ProfilerListener listener);

    /**
     * 结束后的处理
     *
     * @param listener
     * @return
     */
    LifeCycle onFinished(ProfilerListener listener);
}
