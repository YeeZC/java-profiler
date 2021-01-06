package me.zyee.profiler.agent;

import java.lang.instrument.Instrumentation;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/6
 */
public class ProfilerAgent {

    public static void premain(String[] args, Instrumentation inst) {
        agentmain(args, inst);
    }

    public static void agentmain(String[] args, Instrumentation inst) {

    }
}
