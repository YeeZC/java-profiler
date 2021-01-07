package me.zyee.java.profiler.agent;

import me.zyee.java.profiler.impl.DefaultProfilerCore;
import me.zyee.java.profiler.impl.ProfileJUnitRunner;
import org.junit.Test;

import java.io.IOException;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class MethodAgentTest {
    @Test
    public void test() throws IOException {
        final DefaultProfilerCore core = new DefaultProfilerCore();
        core.profile(new ProfileJUnitRunner(TestClass.class));
    }
}