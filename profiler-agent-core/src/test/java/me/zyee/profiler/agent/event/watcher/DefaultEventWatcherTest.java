package me.zyee.profiler.agent.event.watcher;

import java.lang.instrument.Instrumentation;
import me.zyee.profiler.agent.event.Event;
import me.zyee.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.profiler.agent.event.listener.EventListener;
import me.zyee.profiler.spy.Spy;
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
        Spy.init("profiler", handler);
        EventWatcher watcher = new DefaultEventWatcher(install, handler);
        final int watch = watcher.watch("me.zyee.profiler.agent.event.watcher.TestCase#print", new EventListener() {
            @Override
            public boolean onEvent(Event event) throws Throwable {
                System.out.println(event);
                return true;
            }
        }, Event.Type.BEFORE);
        JUnitCore.runClasses(TestCase.class);

    }
}