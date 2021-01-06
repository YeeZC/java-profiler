package me.zyee.profiler.agent.event.handler;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.zyee.profiler.agent.event.Before;
import me.zyee.profiler.agent.event.Event;
import me.zyee.profiler.agent.event.Return;
import me.zyee.profiler.agent.event.Throws;
import me.zyee.profiler.agent.event.listener.EventListener;
import me.zyee.profiler.agent.event.listener.EventListenerWrapper;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class DefaultEventHandler implements EventHandler {
    private final Map<Integer, EventListener> listeners = new ConcurrentHashMap<>();

    @Override
    public void register(int id, EventListener listener, Event.Type[] types) {
        listeners.putIfAbsent(id, EventListenerWrapper.builder()
                .setDelegate(listener)
                .setTypes(types)
                .build());
    }

    @Override
    public void unRegister(int id) {
        listeners.remove(id);
    }

    @Override
    public void onBefore(int listenId, ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args) throws Throwable {
        if (listeners.containsKey(listenId)) {
            final Before event = Before.builder()
                    .setId(listenId)
                    .setTriggerLoader(loader)
                    .setTriggerMethod(methodName)
                    .setTrigger(target)
                    .setArgs(args).build();
            try {
                listeners.get(listenId).onEvent(event);
            } finally {
                cleanEvent(event);
            }
        }
    }

    @Override
    public void onReturn(int listenId, Object returnObject) throws Throwable {
        if (listeners.containsKey(listenId)) {
            final Return event = Return.builder()
                    .setId(listenId)
                    .setReturnObject(returnObject)
                    .build();
            try {
                listeners.get(listenId).onEvent(event);
            } finally {
                cleanEvent(event);
            }
        }
    }

    @Override
    public void onThrows(int listenId, Throwable throwable) throws Throwable {
        if (listeners.containsKey(listenId)) {
            final Throws event = Throws.builder()
                    .setId(listenId)
                    .setReturnObject(throwable)
                    .build();
            try {
                listeners.get(listenId).onEvent(event);
            } finally {
                cleanEvent(event);
            }
        }
    }

    void cleanEvent(Event event) {
        switch (event.type()) {
            case BEFORE: {
                unCaughtSetClassDeclaredJavaFieldValue(event, "trigger", null);
                unCaughtSetClassDeclaredJavaFieldValue(event, "args", null);
                break;
            }
            case RETURN: {
                unCaughtSetClassDeclaredJavaFieldValue(event, "returnObject", null);
                break;
            }
            case THROWS: {
                unCaughtSetClassDeclaredJavaFieldValue(event, "throwable", null);
                break;
            }
            default:
        }
    }

    void unCaughtSetClassDeclaredJavaFieldValue(Object target, String fieldName, Object fieldValue) {
        final Field trigger = FieldUtils.getField(target.getClass(), fieldName);
        try {
            FieldUtils.writeField(trigger, target, fieldValue, true);
        } catch (IllegalAccessException ignore) {
        }
    }
}
