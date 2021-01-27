package me.zyee.java.profiler.agent.event.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.zyee.java.profiler.agent.event.listener.EventListenerWrapper;
import me.zyee.java.profiler.event.Before;
import me.zyee.java.profiler.event.CallBefore;
import me.zyee.java.profiler.event.CallReturn;
import me.zyee.java.profiler.event.CallThrows;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.Line;
import me.zyee.java.profiler.event.Return;
import me.zyee.java.profiler.event.Throws;
import me.zyee.java.profiler.event.listener.EventListener;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class DefaultEventHandler implements EventHandler {
    private final Map<Integer, EventListener> listeners = new ConcurrentHashMap<>();

    @Override
    public void register(int id, EventListener listener, Event.Type[] types) {
        listeners.putIfAbsent(id, new EventListenerWrapper(listener));
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
                event.destroy();
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
                event.destroy();
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
                event.destroy();
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
                event.destroy();
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
                event.destroy();
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
                event.destroy();
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
                event.destroy();
            }
        }
    }

    @Override
    public void onEntry(int listenId) throws Throwable {
        if (listeners.containsKey(listenId)) {
            listeners.get(listenId).onEvent(Event.Entry);
        }
    }
}
