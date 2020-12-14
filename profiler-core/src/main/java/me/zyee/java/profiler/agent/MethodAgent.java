package me.zyee.java.profiler.agent;

import java.lang.instrument.Instrumentation;
import me.zyee.java.profiler.annotation.Atoms;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class MethodAgent {
    public static Instrumentation inst;
    public static void premain(String[] args, Instrumentation inst) {
        agentmain(args, inst);
    }

    public static void agentmain(String[] args, Instrumentation inst) {
        System.out.println("Agent");
        MethodAgent.inst = inst;
        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) ->
                builder.method(ElementMatchers.isAnnotatedWith(Atoms.class))
                        .intercept(MethodDelegation.to(AgentInterceptor.class));

        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform(transformer)
                .installOn(inst);
    }
}
