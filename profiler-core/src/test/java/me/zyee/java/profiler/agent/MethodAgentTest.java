package me.zyee.java.profiler.agent;

import java.io.IOException;
import java.nio.file.Paths;
import me.zyee.java.profiler.impl.Core;
import me.zyee.java.profiler.impl.ProfileJUnitRunner;
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
        final Core core = Core.builder()
                .setWarmups(1)
                .setReportPath(Paths.get(System.getProperty("user.dir")))
                .build();
        core.profile(new ProfileJUnitRunner(TestClass.class));
    }
}