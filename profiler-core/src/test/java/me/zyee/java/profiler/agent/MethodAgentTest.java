package me.zyee.java.profiler.agent;

import java.io.IOException;
import me.zyee.java.profiler.impl.ProfileJUnitRunner;
import me.zyee.java.profiler.impl.StrictProfilerCore;
import org.junit.Test;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class MethodAgentTest {
    @Test
    public void test() throws IOException {
        System.out.println(TestClass.class);
        final StrictProfilerCore core = StrictProfilerCore.builder().addPattern("me.zyee.java.profiler.agent.TestClass#print")
                .build();
        core.profile(new ProfileJUnitRunner(TestClass.class));
        System.out.println(StrictInterceptor.counter);
    }
}