package me.zyee.java.profiler.agent.event.handler;

import me.zyee.java.profiler.event.Before;
import me.zyee.java.profiler.event.CallBefore;
import me.zyee.java.profiler.event.CallReturn;
import me.zyee.java.profiler.event.CallThrows;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.Line;
import me.zyee.java.profiler.event.Return;
import me.zyee.java.profiler.event.Throws;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.spy.Spy;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class DefaultEventHandler implements EventHandler {
    private final EventListener[] listeners = new EventListener[Spy.START];

    @Override
    public void register(int id, EventListener listener, Event.Type[] types, boolean checkWarmup) {
        final int idx = id % Spy.START;
        if (listeners[idx] == null) {
            listeners[idx] = listener;
        }
//        listeners.putIfAbsent(id, new EventListenerWrapper(listener));
    }

    @Override
    public void unRegister(int id) {
        listeners[id % Spy.START] = null;
    }

    @Override
    public void onBefore(int listenId, ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args) throws Throwable {

        final int idx = listenId % Spy.START;
        if (listeners[idx] != null) {
            final Before event = Before.builder()
                    .setId(listenId)
                    .setTriggerClass(className)
                    .setTriggerLoader(loader)
                    .setTriggerMethod(methodName)
                    .setTrigger(target)
                    .setTriggerMethodSign(methodDesc)
                    .setArgs(args).build();
            try {
                listeners[idx].onEvent(event);
            } finally {
                event.destroy();
            }
        }
    }

    @Override
    public void onReturn(int listenId, Object returnObject) throws Throwable {
        final int idx = listenId % Spy.START;
        if (listeners[idx] != null) {
            final Return event = Return.builder()
                    .setId(listenId)
                    .setReturnObject(returnObject)
                    .build();
            try {
                listeners[idx].onEvent(event);
            } finally {
                event.destroy();
            }
        }
    }

    @Override
    public void onThrows(int listenId, Throwable throwable) throws Throwable {
        final int idx = listenId % Spy.START;
        if (listeners[idx] != null) {
            final Throws event = Throws.builder()
                    .setId(listenId)
                    .setThrowable(throwable)
                    .build();
            try {
                listeners[idx].onEvent(event);
            } finally {
                event.destroy();
            }
        }
    }

    @Override
    public void onCallBefore(int listenId, String className, String methodName, String desc, int lineNumber) throws Throwable {
        final int idx = listenId % Spy.START;
        final EventListener listener = listeners[idx];
        if (null != listener) {
            final CallBefore event = CallBefore.builder()
                    .setId(listenId)
                    .setTriggerClass(className)
                    .setTriggerMethod(methodName)
                    .setTriggerMethodSign(desc)
                    .setLineNumber(lineNumber)
                    .build();
            try {
                listener.onEvent(event);
            } finally {
                event.destroy();
            }
        }
    }

    @Override
    public void onCallReturn(int listenId, int lineNumber) throws Throwable {
        final int idx = listenId % Spy.START;
        if (listeners[idx] != null) {
            final CallReturn event = CallReturn.builder()
                    .setId(listenId)
                    .setLineNumber(lineNumber)
                    .build();
            try {
                listeners[idx].onEvent(event);
            } finally {
                event.destroy();
            }
        }
    }

    @Override
    public void onCallThrows(int listenId, int lineNumber, Throwable throwMsg) throws Throwable {
        final int idx = listenId % Spy.START;
        if (listeners[idx] != null) {
            final CallThrows event = CallThrows.builder()
                    .setId(listenId)
                    .setThrowable(throwMsg)
                    .build();
            try {
                listeners[idx].onEvent(event);
            } finally {
                event.destroy();
            }
        }
    }

    @Override
    public void onLine(int listenId, int lineNumber) throws Throwable {
        final int idx = listenId % Spy.START;
        if (listeners[idx] != null) {
            final Line event = Line.builder()
                    .setId(listenId)
                    .setLineNumber(lineNumber)
                    .build();
            try {
                listeners[idx].onEvent(event);
            } finally {
                event.destroy();
            }
        }
    }

    @Override
    public void onEntry(int listenId) throws Throwable {
        final int idx = listenId % Spy.START;
        if (listeners[idx] != null) {
            listeners[idx].onEvent(Event.Entry);
        }
    }
}
