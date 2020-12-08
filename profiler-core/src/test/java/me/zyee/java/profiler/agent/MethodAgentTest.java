package me.zyee.java.profiler.agent;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Test;
import org.junit.runner.JUnitCore;

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
        JUnitCore.runClasses(TestClass.class);
    }
}