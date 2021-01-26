package me.zyee.java.profiler.agent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import javax.annotation.Resource;
import me.zyee.java.profiler.agent.converter.Converters;
import me.zyee.java.profiler.agent.converter.string.FromStringConverter;
import me.zyee.java.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.java.profiler.agent.event.watcher.DefaultEventWatcher;
import me.zyee.java.profiler.agent.listener.ReportListener;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.DefaultBehaviorFilter;
import me.zyee.java.profiler.spy.Spy;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
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
            Method isWarmup = MethodUtils.getMatchingMethod(core, "isWarmup");
            Injector.isWarmup = () -> {
                try {
                    return (Boolean) isWarmup.invoke(null);
                } catch (Throwable e) {
                    return false;
                }
            };
            final Object instance = MethodUtils.invokeStaticMethod(core, "getInstance");
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
            final int watchId = watcher.watch(new DefaultBehaviorFilter("me.zyee.java.profiler.report.Report#output"),
                    new ReportListener(), Event.Type.BEFORE);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> watcher.delete(watchId)));
        }
    }

    public static <T> T fromArgs(String arg, T configure) {
        if (null == arg) {
            return configure;
        }
        final byte[] bytes = arg.replace(";", "\n").getBytes(StandardCharsets.UTF_8);
        Properties properties = new Properties();
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            properties.load(is);
        } catch (IOException e) {
            return configure;
        }
        final Field[] fields = FieldUtils.getAllFields(configure.getClass());
        for (Field field : fields) {
            try {
                final String property = properties.getProperty(field.getName());
                if (StringUtils.isNotEmpty(property)) {
                    final FromStringConverter<?> converter = Converters.create(field.getType());
                    FieldUtils.writeField(field, configure, converter.convert(property), true);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return configure;
    }
}
