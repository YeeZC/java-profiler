package me.zyee.profiler.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class ProfilerAgent {

    public static void premain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        agentmain(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Initializer.init(inst);
    }
}
