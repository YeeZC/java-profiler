package me.zyee.java.profiler.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.annotation.Resource;
import me.zyee.java.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.java.profiler.agent.event.watcher.DefaultEventWatcher;
import me.zyee.java.profiler.agent.listener.ModuleListener;
import me.zyee.java.profiler.agent.listener.ReportListener;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.DefaultBehaviorFilter;
import me.zyee.java.profiler.spy.Spy;
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
    public static Supplier<Boolean> isWarmup;

    public static void init(Instrumentation inst) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> core = ClassUtils.getClass(CLASS_CORE_MODULE);
        if (null != core) {
            final Object instance = MethodUtils.invokeStaticMethod(core, "getInstance");
            final AtomicBoolean warmup = (AtomicBoolean) FieldUtils.readField(instance, "warmup", true);
            Injector.isWarmup = () -> {
                try {
                    return warmup.get();
                } catch (Throwable e) {
                    return false;
                }
            };
            final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(core, Resource.class);
            final DefaultEventHandler handler = new DefaultEventHandler();
            EventWatcher watcher = new DefaultEventWatcher(inst, handler);
            for (Field field : fields) {
                final Class<?> type = field.getType();
                if (ClassUtils.isAssignable(EventWatcher.class, type)
                        || ClassUtils.isAssignable(type, EventWatcher.class)) {
                    FieldUtils.writeField(field, instance, watcher, true);
                } else if (ClassUtils.isAssignable(Instrumentation.class, type)
                        || ClassUtils.isAssignable(type, Instrumentation.class)) {
                    FieldUtils.writeField(field, instance, inst, true);
                }
            }
            Spy.init(handler);
            final int report = watcher.watch(new DefaultBehaviorFilter("me.zyee.java.profiler.report.Report#output"),
                    new ReportListener(), Event.Type.BEFORE);
            final int module = watcher.watch(new DefaultBehaviorFilter("me.zyee.java.profiler.module.Module#enable"),
                    new ModuleListener(watcher), Event.Type.BEFORE);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                watcher.delete(report);
                watcher.delete(module);
            }));
        }
    }

}
