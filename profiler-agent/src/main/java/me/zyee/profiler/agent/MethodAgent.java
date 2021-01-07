package me.zyee.profiler.agent;

import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.profiler.agent.event.watcher.DefaultEventWatcher;
import me.zyee.profiler.spy.Spy;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.instrument.Instrumentation;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class MethodAgent {
    public static Instrumentation inst;
    private static final String CLASS_FINDER = "me.zyee.java.profiler.utils.ProfilerHelper";

    public static void premain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException {
        agentmain(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException {
        System.out.println("Agent");
        MethodAgent.inst = inst;
        final Class<?> finder = ClassUtils.getClass(CLASS_FINDER);
        System.out.println(finder);
        FieldUtils.writeStaticField(finder, "inst", inst, true);
        final DefaultEventHandler handler = new DefaultEventHandler();
        System.out.println(handler);
        EventWatcher watcher = new DefaultEventWatcher(inst, handler);
        System.out.println(watcher);
        Spy.init(handler);
        System.out.println("init");
        FieldUtils.writeStaticField(finder, "watcher", watcher, true);
        System.out.println("watcher");
//        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) ->
//                builder.method(ElementMatchers.isAnnotatedWith(Profile.class))
//                        .intercept(MethodDelegation.to(AgentInterceptor.class));
//
//        new AgentBuilder.Default()
//                .type(ElementMatchers.any())
//                .transform(transformer)
//                .installOn(inst);
    }
}
