package me.zyee.java.profiler.agent.utils;

import com.google.common.reflect.Reflection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.zyee.java.profiler.agent.config.AgentConfigure;
import me.zyee.java.profiler.agent.enhancer.Enhancer;
import me.zyee.java.profiler.agent.enhancer.EventEnhancer;
import me.zyee.java.profiler.agent.hardware.Hardware;
import me.zyee.java.profiler.agent.hardware.OshiHardware;
import me.zyee.java.profiler.filter.CallBeforeFilter;
import me.zyee.java.profiler.operation.AtomGroupType;
import me.zyee.java.profiler.operation.AtomGroups;
import me.zyee.java.profiler.operation.AtomOperation;
import me.zyee.java.profiler.operation.CopyAtomGroup;
import me.zyee.java.profiler.operation.impl.DefaultAtomOperation;
import me.zyee.java.profiler.utils.StringHelper;
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

    public static void init(String args) throws IOException {
        final AgentConfigure configure = StringHelper.fromArgs(args, new AgentConfigure());
        dump = configure.isDumpClassFile();
        if (null != configure.getLibPath()) {
            PROFILER_LOADER = ProfilerClassLoader.newInstance(configure.getLibPath());
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
                            AtomGroups.register(value, Reflection.newProxy(CopyAtomGroup.class, (proxy, method, arg) ->
                                    {
                                        final Object res = MethodUtils.invokeMethod(o, true, method.getName(), arg);
                                        if (res instanceof List) {
                                            List<AtomOperation> result = new ArrayList<>();
                                            ((List<?>) res).forEach(item -> {
                                                final String str = StringHelper.toString(item);
                                                result.add(StringHelper.fromArgs(str, DefaultAtomOperation.builder()).build());
                                            });
                                            return result;
                                        }
                                        if (res instanceof Optional && ((Optional<?>) res).isPresent()) {
                                            final Object item = ((Optional<?>) res).get();
                                            final String str = StringHelper.toString(item);
                                            return Optional.of(StringHelper.fromArgs(str, DefaultAtomOperation.builder()).build());
                                        }
                                        return res;
                                    }
                            ));
                            break;
                        default:
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
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

        return new StructureWrapper(structure);
    }

    public static Hardware newHardware() {
        try {
            final Class<?> hardware = PROFILER_LOADER.loadClass("me.zyee.java.profiler.agent.hardware.OshiHardware");
            final Object delegate = hardware.newInstance();
            return Reflection.newProxy(Hardware.class,
                    (proxy, method, args) -> MethodUtils.invokeMethod(delegate,
                            true, method.getName(), args));
        } catch (Exception e) {
            return new OshiHardware();
        }
    }
}
