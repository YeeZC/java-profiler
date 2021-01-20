package me.zyee.java.profiler.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import me.zyee.java.profiler.agent.utils.Initializer;

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
        Initializer.init(args);
        Injector.init(inst);
    }
}
