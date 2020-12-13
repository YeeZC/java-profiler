package me.zyee.java.profiler.agent;

import me.zyee.java.profiler.BaseProfilerCore;
import me.zyee.java.profiler.impl.ProfileJUnitRunner;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Test;

import java.lang.instrument.Instrumentation;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class MethodAgentTest {
    @Test
    public void test() {
        final Instrumentation install = ByteBuddyAgent.install();
        MethodAgent.agentmain(new String[]{}, install);
        final BaseProfilerCore core = new BaseProfilerCore() {
        };
        core.profile(new ProfileJUnitRunner(TestClass.class));
    }
}