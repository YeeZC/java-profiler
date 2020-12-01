package me.zyee.java.profiler;

import java.util.concurrent.Future;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public interface Profiler {
    /**
     * 执行Profile
     *
     * @param testCases
     * @return
     */
    Future<?> profile(Class<?>... testCases);

    /**
     * 创建Builder
     *
     * @return
     */
    default ProfilerBuilder builder() {
        return new ProfilerBuilder();
    }
}
