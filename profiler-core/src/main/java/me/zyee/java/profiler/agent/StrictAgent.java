package me.zyee.java.profiler.agent;

import java.lang.instrument.Instrumentation;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class StrictAgent {
    public static void premain(String[] args, Instrumentation inst) {
        agentmain(args, inst);
    }

    public static void agentmain(String[] args, Instrumentation inst) {
        for (String pattern : args) {
            final String[] split = pattern.split("#");
            AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) ->
                    builder.method(ElementMatchers.nameContains(split[1]))
                            .intercept(MethodDelegation.to(StrictInterceptor.class));

            new AgentBuilder.Default()
                    .type(ElementMatchers.nameContainsIgnoreCase(split[0]))
                    .transform(transformer)
                    .installOn(inst);

        }

    }
}
