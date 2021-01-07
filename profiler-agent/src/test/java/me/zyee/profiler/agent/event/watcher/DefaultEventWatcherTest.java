package me.zyee.profiler.agent.event.watcher;

import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.profiler.spy.Spy;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/7
 */
public class DefaultEventWatcherTest {

    @Test
    public void test() {
        final Instrumentation install = ByteBuddyAgent.install();
        final DefaultEventHandler handler = new DefaultEventHandler();
        Spy.init(handler);
        EventWatcher watcher = new DefaultEventWatcher(install, handler);
        AtomicInteger counter = new AtomicInteger();
        final int watch = watcher.watch("me.zyee.profiler.agent.event.watcher.TestCase#print", new EventListener() {
            @Override
            public boolean onEvent(Event event) throws Throwable {
                counter.incrementAndGet();
                return true;
            }
        }, Event.Type.BEFORE);
        JUnitCore.runClasses(TestCase.class);
        System.err.println(counter);
    }
}