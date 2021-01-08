package me.zyee.profiler.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.profiler.agent.event.watcher.DefaultEventWatcher;
import me.zyee.profiler.spy.Spy;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class MethodAgent {
    public static Instrumentation inst;
    private static final String CLASS_FINDER = "me.zyee.java.profiler.utils.ProfilerHelper";

    public static void premain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        agentmain(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MethodAgent.inst = inst;
        final Class<?> finder = ClassUtils.getClass(CLASS_FINDER);
        final Object instance = MethodUtils.invokeStaticMethod(finder, "getInstance");
        FieldUtils.writeField(instance, "inst", inst, true);
        final DefaultEventHandler handler = new DefaultEventHandler();
        EventWatcher watcher = new DefaultEventWatcher(inst, handler);
        Spy.init(handler);
        FieldUtils.writeField(instance, "watcher", watcher, true);
    }
}
