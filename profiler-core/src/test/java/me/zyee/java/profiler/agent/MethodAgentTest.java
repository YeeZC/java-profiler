package me.zyee.java.profiler.agent;

import java.io.IOException;
import java.nio.file.Paths;
import me.zyee.java.profiler.filter.DefaultBehaviorFilter;
import me.zyee.java.profiler.impl.DefaultProfilerCore;
import me.zyee.java.profiler.impl.ProfileJUnitRunner;
import me.zyee.java.profiler.module.CoreModule;
import org.junit.Test;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class MethodAgentTest {
    @Test
    public void test() throws IOException {
        final DefaultProfilerCore core = DefaultProfilerCore.builder()
                .setReportPath(Paths.get(System.getProperty("user.dir")))
                .build();
        CoreModule.watch(new DefaultBehaviorFilter("me.zyee.java.profiler.agent.TestClass#print"), event -> {
            System.out.println(event);
            return false;
        });
        core.profile(new ProfileJUnitRunner(TestClass.class));
    }
}