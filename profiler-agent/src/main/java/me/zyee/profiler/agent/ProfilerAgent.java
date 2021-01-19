package me.zyee.profiler.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import me.zyee.profiler.agent.loader.ProfilerClassLoader;

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
        try {
            ProfilerClassLoader.create(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Initializer.init(inst);
    }
}
