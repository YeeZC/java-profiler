package me.zyee.java.profiler.agent.event.watcher;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicInteger;
import me.zyee.java.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.java.profiler.event.Before;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.CallBeforeFilter;
import me.zyee.java.profiler.filter.DefaultBehaviorFilter;
import me.zyee.java.profiler.spy.Spy;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Test;
import org.junit.runner.JUnitCore;

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
        try {
            final int watch = watcher.watch(new DefaultBehaviorFilter("me.zyee.java.profiler.agent.event.watcher.TestCase#print"), new CallBeforeFilter() {
                        @Override
                        public boolean methodFilter(String caller, String name, String descriptor) {
                            return "me.zyee.java.profiler.agent.event.watcher.TestCase#plusRandom".equals(caller + "#" + name);
                        }
                    },
                    new EventListener() {
                        @Override
                        public boolean onEvent(Event event) throws Throwable {
                            if (event.type() == Event.Type.BEFORE) {
                                counter.incrementAndGet();
                            }
                            System.out.println(event);
                            return true;
                        }
                    }, Event.Type.ENTRY, Event.Type.CALL_BEFORE);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        JUnitCore.runClasses(TestCase.class);
        System.err.println(counter);
    }
}