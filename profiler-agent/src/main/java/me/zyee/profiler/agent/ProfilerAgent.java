package me.zyee.profiler.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import me.zyee.profiler.agent.classloader.ProfilerClassLoader;

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
            final ProfilerClassLoader loader = ProfilerClassLoader.create(Paths.get(args));
            Thread.currentThread().setContextClassLoader(loader);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Initializer.init(inst);
    }
}
