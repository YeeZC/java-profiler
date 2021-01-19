package me.zyee.profiler.agent.event.handler;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.zyee.java.profiler.event.Before;
import me.zyee.java.profiler.event.CallBefore;
import me.zyee.java.profiler.event.CallReturn;
import me.zyee.java.profiler.event.CallThrows;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.Line;
import me.zyee.java.profiler.event.Return;
import me.zyee.java.profiler.event.Throws;
import me.zyee.java.profiler.event.annotation.AutoClear;
import me.zyee.java.profiler.event.listener.EventListener;
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
                    .setTriggerClass(className)
                    .setTriggerLoader(loader)
                    .setTriggerMethod(methodName)
                    .setTrigger(target)
                    .setTriggerMethodSign(methodDesc)
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
                    .setThrowable(throwable)
                    .build();
            try {
                listeners.get(listenId).onEvent(event);
            } finally {
                cleanEvent(event);
            }
        }
    }

    @Override
    public void onCallBefore(int listenId, String className, String methodName, String desc, int lineNumber) throws Throwable {
        if (listeners.containsKey(listenId)) {
            final CallBefore event = CallBefore.builder()
                    .setId(listenId)
                    .setTriggerClass(className)
                    .setTriggerMethod(methodName)
                    .setTriggerMethodSign(desc)
                    .setLineNumber(lineNumber)
                    .build();
            try {
                listeners.get(listenId).onEvent(event);
            } finally {
                cleanEvent(event);
            }
        }
    }

    @Override
    public void onCallReturn(int listenId, int lineNumber) throws Throwable {
        if (listeners.containsKey(listenId)) {
            final CallReturn event = CallReturn.builder()
                    .setId(listenId)
                    .setLineNumber(lineNumber)
                    .build();
            try {
                listeners.get(listenId).onEvent(event);
            } finally {
                cleanEvent(event);
            }
        }
    }

    @Override
    public void onCallThrows(int listenId, int lineNumber, Throwable throwMsg) throws Throwable {
        if (listeners.containsKey(listenId)) {
            final CallThrows event = CallThrows.builder()
                    .setId(listenId)
                    .setThrowable(throwMsg)
                    .build();
            try {
                listeners.get(listenId).onEvent(event);
            } finally {
                cleanEvent(event);
            }
        }
    }

    @Override
    public void onLine(int listenId, int lineNumber) throws Throwable {
        if (listeners.containsKey(listenId)) {
            final Line event = Line.builder()
                    .setId(listenId)
                    .setLineNumber(lineNumber)
                    .build();
            try {
                listeners.get(listenId).onEvent(event);
            } finally {
                cleanEvent(event);
            }
        }
    }

    void cleanEvent(Event event) {
        final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(event.getClass(), AutoClear.class);
        for (Field field : fields) {
            try {
                FieldUtils.writeField(field, event, null, true);
            } catch (IllegalAccessException ignore) {
            }
        }
    }

}
