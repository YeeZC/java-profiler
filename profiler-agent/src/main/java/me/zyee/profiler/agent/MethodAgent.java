package me.zyee.profiler.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import me.zyee.java.profiler.bean.Cpu;
import me.zyee.java.profiler.bean.Net;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.utils.FileUtils;
import me.zyee.profiler.agent.event.handler.DefaultEventHandler;
import me.zyee.profiler.agent.event.watcher.DefaultEventWatcher;
import me.zyee.profiler.spy.Spy;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class MethodAgent {
    public static Instrumentation inst;
    private static final String CLASS_CORE_MODULE = "me.zyee.java.profiler.module.CoreModule";

    public static void premain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, SigarException {
        agentmain(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, SigarException {
        loadSigar(args);
        MethodAgent.inst = inst;
        final Class<?> finder = ClassUtils.getClass(CLASS_CORE_MODULE);
        final Object instance = MethodUtils.invokeStaticMethod(finder, "getInstance");
        final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(finder, Resource.class);
        final DefaultEventHandler handler = new DefaultEventHandler();
        EventWatcher watcher = new DefaultEventWatcher(inst, handler);
        Sigar sigar = new Sigar();
        for (Field field : fields) {
            final Class<?> type = field.getType();
            if (ClassUtils.isAssignable(EventWatcher.class, type)
                    || ClassUtils.isAssignable(type, EventWatcher.class)) {
                FieldUtils.writeField(field, instance, watcher, true);
            } else if (ClassUtils.isAssignable(Instrumentation.class, type)
                    || ClassUtils.isAssignable(type, Instrumentation.class)) {
                FieldUtils.writeField(field, instance, inst, true);
            } else if (type.equals(Cpu.class)) {
                final CpuInfo cpuInfo = sigar.getCpuInfoList()[0];
                final int mhz = cpuInfo.getMhz();
                final Cpu cpu = Cpu.builder().setFreq(mhz * 1000L * 1000L)
                        .setLogical(cpuInfo.getTotalCores())
                        .setPhysical(cpuInfo.getTotalCores() / 2)
                        .build();
                FieldUtils.writeField(field, instance, cpu, true);
            } else if (ClassUtils.isAssignable(List.class, type)
                    || ClassUtils.isAssignable(type, List.class)) {
                final Resource resource = field.getAnnotation(Resource.class);
                if ("nets".equals(resource.name())) {
                    Map<String, NetInterfaceStat> stats = new HashMap<>();
                    for (String name : sigar.getNetInterfaceList()) {
                        stats.put(name, sigar.getNetInterfaceStat(name));
                    }
                    final List<Net> collect = stats.entrySet().stream().filter(stat -> stat.getValue().getSpeed() > 0)
                            .map(stat -> Net.builder().setName(stat.getKey()).setSpeed(stat.getValue().getSpeed()).build())
                            .collect(Collectors.toList());
                    FieldUtils.writeField(field, instance, Collections.unmodifiableList(collect), true);
                }

            }
        }
        Spy.init(handler);
    }

    private static void loadSigar(String dir) {
        final String property = System.getProperty("java.library.path");
        Path path = (StringUtils.isEmpty(dir) ?
                Paths.get(System.getProperty("java.io.tmpdir"), "me.zyee.java.profiler") : Paths.get(dir)).resolve("sigar");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                final URL resource = MethodAgent.class.getResource("/sigar/lib");
                String url = resource.getPath();
                if (url.contains("jar")) {
                    loadLibFromJar(path, url);
                } else {
                    loadLibFromFile(path, resource);
                }

            } catch (IOException ignore) {
            }
        }
        if (null == property || property.trim().length() == 0) {
            System.setProperty("java.library.path", path.toString());
        } else {
            System.setProperty("java.library.path", property + File.pathSeparator + path);
        }
    }

    private static void loadLibFromFile(Path path, URL resource) {
        final File src = new File(resource.getFile());
        final File[] children = src.listFiles();
        Optional.ofNullable(children).ifPresent(files -> {
            for (File file : files) {
                try {
                    Files.write(path.resolve(file.getName()), Files.readAllBytes(file.toPath()));
                } catch (Exception ignore) {
                }
            }
        });
    }

    private static void loadLibFromJar(Path path, String url) {
        final String[] paths = url.split("!");
        String jarFilePath = paths[0].substring(paths[0].indexOf("/"));
        String packagePath = paths[1].substring(1);
        try (final JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.startsWith(packagePath) && !jarEntry.isDirectory()) {
                    Files.write(path.resolve(entryName.substring(entryName.lastIndexOf("/") + 1))
                            , FileUtils.readAll(jarFile.getInputStream(jarEntry)));
                }
            }
        } catch (Exception ignore) {
        }
    }
}
