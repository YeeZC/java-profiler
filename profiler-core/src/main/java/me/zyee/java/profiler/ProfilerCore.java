package me.zyee.java.profiler;

import java.io.IOException;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public interface ProfilerCore {
    /**
     * 执行Profile
     *
     * @param runner
     */
    void profile(Runner runner) throws IOException;
}
