package me.zyee.java.profiler.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import me.zyee.java.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.java.profiler.agent.event.watcher.DefaultEventWatcher;
import me.zyee.java.profiler.agent.utils.Hardware;
import me.zyee.java.profiler.agent.utils.Initializer;
import me.zyee.java.profiler.bean.Cpu;
import me.zyee.java.profiler.bean.IOSpeed;
import me.zyee.java.profiler.bean.IOSpeedType;
import me.zyee.java.profiler.bean.Net;
import me.zyee.java.profiler.event.watcher.EventWatcher;
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

    public static void init(Instrumentation inst) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> core = ClassUtils.getClass(CLASS_CORE_MODULE);
        if (null != core) {
            final Object instance = MethodUtils.invokeStaticMethod(core, "getInstance");
            final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(core, Resource.class);
            final DefaultEventHandler handler = new DefaultEventHandler();
            EventWatcher watcher = new DefaultEventWatcher(inst, handler);
            final Hardware hardware = Initializer.newHardware();

            for (Field field : fields) {
                final Class<?> type = field.getType();
                if (ClassUtils.isAssignable(EventWatcher.class, type)
                        || ClassUtils.isAssignable(type, EventWatcher.class)) {
                    FieldUtils.writeField(field, instance, watcher, true);
                } else if (ClassUtils.isAssignable(Instrumentation.class, type)
                        || ClassUtils.isAssignable(type, Instrumentation.class)) {
                    FieldUtils.writeField(field, instance, inst, true);
                } else if (type.equals(Cpu.class)) {
//
                    final Cpu cpu = Cpu.builder().setFreq(hardware.getProcessorFreq())
                            .setLogical(hardware.getLogicalProcessorCount())
                            .setPhysical(hardware.getPhysicalProcessorCount())
                            .build();
                    FieldUtils.writeField(field, instance, cpu, true);
                } else if (ClassUtils.isAssignable(List.class, type)
                        || ClassUtils.isAssignable(type, List.class)) {
                    final Resource resource = field.getAnnotation(Resource.class);
                    switch (resource.name()) {
                        case "nets": {
                            final Map<String, Long> ifs = hardware.getNetIfs();
                            final List<Net> collect = ifs.entrySet().stream()
                                    .filter(nif -> nif.getValue() > 0)
                                    .map(nif -> Net.builder().setName(nif.getKey()).setSpeed(nif.getValue()).build())
                                    .collect(Collectors.toList());
                            FieldUtils.writeField(field, instance, Collections.unmodifiableList(collect), true);
                            break;
                        }
                        case "speed": {
                            List<IOSpeed> speeds = new ArrayList<>();
                            speeds.add(IOSpeed.builder()
                                    .setName("堆内存写")
                                    .setType(IOSpeedType.HEAP_W)
                                    .setSpeed(hardware.heapWriteSpeed())
                                    .build());
                            speeds.add(IOSpeed.builder()
                                    .setName("堆内存读")
                                    .setType(IOSpeedType.HEAP_R)
                                    .setSpeed(hardware.heapReadSpeed())
                                    .build());
                            speeds.add(IOSpeed.builder()
                                    .setName("堆外内存写")
                                    .setType(IOSpeedType.NON_HEAP_W)
                                    .setSpeed(hardware.nonHeapWriteSpeed())
                                    .build());
                            speeds.add(IOSpeed.builder()
                                    .setName("堆外内存读")
                                    .setType(IOSpeedType.NON_HEAP_R)
                                    .setSpeed(hardware.nonHeapReadSpeed())
                                    .build());
                            speeds.add(IOSpeed.builder()
                                    .setName("写文件")
                                    .setType(IOSpeedType.BIO_W)
                                    .setSpeed(hardware.bioWriteSpeed())
                                    .build());
                            speeds.add(IOSpeed.builder()
                                    .setName("读文件")
                                    .setType(IOSpeedType.BIO_R)
                                    .setSpeed(hardware.bioReadSpeed())
                                    .build());
                            speeds.add(IOSpeed.builder()
                                    .setName("NIO写文件（RandomAccessFile）")
                                    .setType(IOSpeedType.NIO_W)
                                    .setSpeed(hardware.nioWriteSpeed())
                                    .build());
                            speeds.add(IOSpeed.builder()
                                    .setName("NIO读文件（RandomAccessFile）")
                                    .setType(IOSpeedType.NIO_R)
                                    .setSpeed(hardware.nioReadSpeed())
                                    .build());
                            FieldUtils.writeField(field, instance, Collections.unmodifiableList(speeds), true);
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
            Spy.init(handler);
        }
    }
}
