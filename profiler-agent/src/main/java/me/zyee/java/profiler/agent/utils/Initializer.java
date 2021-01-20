package me.zyee.java.profiler.agent.utils;

import com.google.common.reflect.Reflection;
import java.io.IOException;
import java.util.Optional;
import me.zyee.java.profiler.agent.config.AgentConfigure;
import me.zyee.java.profiler.agent.enhancer.Enhancer;
import me.zyee.java.profiler.agent.enhancer.EventEnhancer;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/19
 */
public class Initializer {

    private static ProfilerClassLoader PROFILER_LOADER;
    private static Enhancer ENHANCER;

    public static void init(String args) throws IOException {
        final AgentConfigure configure = AgentConfigure.fromArgs(args);
        ENHANCER = new EventEnhancer(configure.isDumpClassFile());
        if (null != configure.getLibPath()) {
            PROFILER_LOADER = ProfilerClassLoader.newInstance(configure.getLibPath());
        }
    }

    public static Enhancer getEnhancer() {
        return Optional.ofNullable(ENHANCER).orElseGet(() -> new EventEnhancer(false));
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
            final Class<?> hardware = PROFILER_LOADER.loadClass("me.zyee.java.profiler.agent.utils.OshiHardware");
            final Object delegate = hardware.newInstance();
            return Reflection.newProxy(Hardware.class,
                    (proxy, method, args) -> MethodUtils.invokeMethod(delegate,
                            true, method.getName(), args));
        } catch (Exception e) {
            return new OshiHardware();
        }
    }
}
