package me.zyee.profiler.agent.transformer;

import me.zyee.profiler.agent.event.Event;
import me.zyee.profiler.agent.event.listener.EventListener;
import me.zyee.profiler.agent.utils.ObjectIds;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * TODO 实现
 *
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/6
 */
public class ProfilerTransformer implements ClassFileTransformer {
    private final String pattern;
    private final EventListener listener;
    private final Event.Type[] listenEvents;
    private final int id;

    public ProfilerTransformer(String pattern, EventListener listener, Event.Type[] listenEvents) {
        this.pattern = pattern;
        this.listener = listener;
        this.listenEvents = listenEvents;
        this.id = ObjectIds.instance.identity(listener);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return new byte[0];
    }

    public int getId() {
        return id;
    }

    public String getPattern() {
        return pattern;
    }
}
