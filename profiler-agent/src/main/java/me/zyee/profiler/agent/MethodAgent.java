package me.zyee.profiler.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import me.zyee.java.profiler.bean.Cpu;
import me.zyee.java.profiler.bean.Memory;
import me.zyee.java.profiler.bean.Net;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.profiler.agent.event.watcher.DefaultEventWatcher;
import me.zyee.profiler.spy.Spy;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class MethodAgent {
    public static Instrumentation inst;
    private static final String CLASS_CORE_MODULE = "me.zyee.java.profiler.module.CoreModule";

    public static void premain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        agentmain(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MethodAgent.inst = inst;
        final Class<?> finder = ClassUtils.getClass(CLASS_CORE_MODULE);
        final Object instance = MethodUtils.invokeStaticMethod(finder, "getInstance");
        final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(finder, Resource.class);
        final DefaultEventHandler handler = new DefaultEventHandler();
        EventWatcher watcher = new DefaultEventWatcher(inst, handler);
        final SystemInfo info = new SystemInfo();
        final HardwareAbstractionLayer hal = info.getHardware();
        for (Field field : fields) {
            final Class<?> type = field.getType();
            if (ClassUtils.isAssignable(EventWatcher.class, type)
                    || ClassUtils.isAssignable(type, EventWatcher.class)) {
                FieldUtils.writeField(field, instance, watcher, true);
            } else if (ClassUtils.isAssignable(Instrumentation.class, type)
                    || ClassUtils.isAssignable(type, Instrumentation.class)) {
                FieldUtils.writeField(field, instance, inst, true);
            } else if (type.equals(Cpu.class)) {
                final CentralProcessor processor = hal.getProcessor();
                final Cpu cpu = Cpu.builder().setFreq(processor.getMaxFreq())
                        .setLogical(processor.getLogicalProcessorCount())
                        .setPhysical(processor.getPhysicalPackageCount())
                        .build();
                FieldUtils.writeField(field, instance, cpu, true);
            } else if (ClassUtils.isAssignable(List.class, type)
                    || ClassUtils.isAssignable(type, List.class)) {
                final Resource resource = field.getAnnotation(Resource.class);
                switch (resource.name()) {
                    case "memories": {
                        final List<Memory> collect = hal.getMemory().getPhysicalMemory().stream().map(PhysicalMemory::getClockSpeed)
                                .map(Memory.builder()::setClockSpeed).map(Memory.Builder::build)
                                .collect(Collectors.toList());
                        FieldUtils.writeField(field, instance, Collections.unmodifiableList(collect), true);
                        break;
                    }
                    case "nets": {
                        final List<Net> collect = hal.getNetworkIFs().stream().filter(net -> net.getSpeed() > 0)
                                .map(net -> Net.builder()
                                        .setName(net.getDisplayName())
                                        .setSpeed(net.getSpeed()).build())
                                .collect(Collectors.toList());
                        FieldUtils.writeField(field, instance, Collections.unmodifiableList(collect), true);
                    }
                    default:
                }

            }
        }
        Spy.init(handler);
    }
}
