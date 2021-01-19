package me.zyee.profiler.agent;

import me.zyee.profiler.agent.core.utils.AgentProxy;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class ProfilerAgent {

    public static void premain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, IOException, UnmodifiableClassException {
        agentmain(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, IOException, UnmodifiableClassException {
        AgentProxy.init(args);
        Injector.init(inst);
    }
}
