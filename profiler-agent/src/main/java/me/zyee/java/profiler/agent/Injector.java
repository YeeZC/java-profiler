package me.zyee.java.profiler.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.annotation.Resource;
import me.zyee.java.profiler.WarmupSwitcher;
import me.zyee.java.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.java.profiler.agent.event.watcher.AgentEventWatcher;
import me.zyee.java.profiler.agent.event.watcher.DefaultEventWatcher;
import me.zyee.java.profiler.agent.listener.ModuleListener;
import me.zyee.java.profiler.agent.listener.ReportListener;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.DefaultBehaviorFilter;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/18
 */
public class Injector {
    private static final String CLASS_CORE_MODULE = "me.zyee.java.profiler.module.CoreModule";

    public static void init(Instrumentation inst) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> core = ClassUtils.getClass(CLASS_CORE_MODULE);
        if (null != core) {
            final Object instance = MethodUtils.invokeStaticMethod(core, "getInstance");
            final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(core, Resource.class);
            final DefaultEventHandler handler = new DefaultEventHandler();
            DefaultWarmupSwitcher switcher = new DefaultWarmupSwitcher(handler);
            AgentEventWatcher watcher = new DefaultEventWatcher(inst, handler);
            for (Field field : fields) {
                final Class<?> type = field.getType();
                if (ClassUtils.isAssignable(EventWatcher.class, type)
                        || ClassUtils.isAssignable(type, EventWatcher.class)) {
                    FieldUtils.writeField(field, instance, watcher, true);
                } else if (ClassUtils.isAssignable(Instrumentation.class, type)
                        || ClassUtils.isAssignable(type, Instrumentation.class)) {
                    FieldUtils.writeField(field, instance, inst, true);
                } else if (type.isAssignableFrom(WarmupSwitcher.class)) {
                    FieldUtils.writeField(field, instance, switcher, true);
                }
            }

            final DefaultEventWatcher system = new DefaultEventWatcher(inst, switcher.getSystem());

            final int report = system.watch(new DefaultBehaviorFilter("me.zyee.java.profiler.report.Report#output"),
                    new ReportListener(), false, Event.Type.BEFORE);
            final int module = system.watch(new DefaultBehaviorFilter("me.zyee.java.profiler.module.Module#enable"),
                    new ModuleListener(watcher), false, Event.Type.BEFORE);
            switcher.getSystem().copyListener(handler);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                system.delete(report);
                system.delete(module);
            }));
        }
    }

}
