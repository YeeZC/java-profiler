package me.zyee.java.profiler.agent.utils;

import com.google.common.reflect.Reflection;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.zyee.java.profiler.agent.config.AgentConfigure;
import me.zyee.java.profiler.agent.enhancer.Enhancer;
import me.zyee.java.profiler.agent.enhancer.EventEnhancer;
import me.zyee.java.profiler.agent.hardware.Hardware;
import me.zyee.java.profiler.agent.hardware.OshiHardware;
import me.zyee.java.profiler.agent.operation.AgentCopyAtomGroup;
import me.zyee.java.profiler.agent.operation.MethodProcess;
import me.zyee.java.profiler.agent.operation.MethodProxy;
import me.zyee.java.profiler.benchmark.operation.CopyAtomGroups;
import me.zyee.java.profiler.filter.CallBeforeFilter;
import me.zyee.java.profiler.operation.AtomGroup;
import me.zyee.java.profiler.operation.AtomGroupType;
import me.zyee.java.profiler.operation.AtomGroups;
import me.zyee.java.profiler.utils.StringHelper;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/19
 */
public class Initializer {

    private static ProfilerClassLoader PROFILER_LOADER;
    private static boolean dump = true;
    private static final Map<Class<? extends MethodProcess>, MethodProcess> PROCESS = new HashMap<>();

    public static void init(String args) throws IOException {
        final AgentConfigure configure = StringHelper.fromArgs(args, new AgentConfigure());
        dump = configure.isDumpClassFile();
        if (null != configure.getLibPath()) {
            PROFILER_LOADER = ProfilerClassLoader.newInstance(configure.getLibPath());
        }
        registerAtomGroup();
    }

    public static Enhancer getEnhancer(CallBeforeFilter callBeforeFilter) {
        return new EventEnhancer(dump, callBeforeFilter);
    }

    public static Structure newStructure(final ClassLoader loader,
                                         final Class<?> classBeingRedefined,
                                         final byte[] srcByteCodeArray) {
        ClassStructure structure = null;
        if (null == classBeingRedefined) {
            structure = new ClassStructureImplByAsm(srcByteCodeArray, loader);
        } else {
            structure = new ClassStructureImplByJDK(classBeingRedefined);
        }

        return new DefaultStructure(structure);
    }

    public static Hardware newHardware() {
        try {
            final Class<?> hardware = PROFILER_LOADER.loadClass("me.zyee.java.profiler.agent.hardware.OshiHardware");
            final Object delegate = hardware.newInstance();
            return proxy(Hardware.class, delegate);
        } catch (Exception e) {
            return new OshiHardware();
        }
    }

    private static void registerAtomGroup() {
        try {
            final Class<?> groups = PROFILER_LOADER.loadClass("me.zyee.java.profiler.benchmark.operation.CopyAtomGroups");
            for (AtomGroupType value : AtomGroupType.values()) {
                switch (value) {
                    case COPY_BYTE_ARRAY:
                    case COPY_INT_ARRAY:
                    case COPY_LONG_ARRAY:
                    case COPY_DOUBLE_ARRAY:
                    case COPY_UNSAFE_ARRAY:
                        final Object o = FieldUtils.readStaticField(groups, value.name(), true);
                        AtomGroups.register(value, proxy(AgentCopyAtomGroup.class, o));
                        break;
                    default:
                }
            }
        } catch (Throwable e) {
            for (AtomGroupType value : AtomGroupType.values()) {
                try {
                    switch (value) {
                        case COPY_BYTE_ARRAY:
                        case COPY_INT_ARRAY:
                        case COPY_LONG_ARRAY:
                        case COPY_DOUBLE_ARRAY:
                        case COPY_UNSAFE_ARRAY:
                            final Object o = FieldUtils.readStaticField(CopyAtomGroups.class, value.name(), true);
                            AtomGroups.register(value, (AtomGroup) o);
                            break;
                        default:
                    }
                } catch (Throwable ignore) {
                }
            }
        }
    }

    private static <T> T proxy(Class<T> inf, Object delegate) {
        final List<Method> methods = MethodUtils.getMethodsListWithAnnotation(inf, MethodProxy.class);
        for (Method method : methods) {
            final MethodProxy proxy = method.getAnnotation(MethodProxy.class);
            if (!PROCESS.containsKey(proxy.value())) {
                try {
                    PROCESS.put(proxy.value(), ConstructorUtils.invokeConstructor(proxy.value(), method));
                } catch (Throwable ignore) {
                }
            }
        }
        return Reflection.newProxy(inf, (proxy, method, args) -> {
            if (method.isAnnotationPresent(MethodProxy.class)) {
                final MethodProxy p = method.getAnnotation(MethodProxy.class);
                if (PROCESS.containsKey(p.value())) {
                    final MethodProcess process = PROCESS.get(p.value());
                    return process.process(delegate, args);
                }
            }
            return MethodUtils.invokeMethod(delegate, true,
                    method.getName(), args);
        });
    }
}
