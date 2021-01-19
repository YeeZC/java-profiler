package me.zyee.profiler.agent.core.utils;

import me.zyee.profiler.agent.core.enhancer.Enhancer;
import me.zyee.profiler.agent.utils.Hardware;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Function;

import static me.zyee.profiler.agent.core.utils.ClassStructureFactory.createClassStructure;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/19
 */
public class AgentProxy {

    private static ProfilerClassLoader PROFILER_LOADER;

    public static void init(String args) throws IOException {
        PROFILER_LOADER = ProfilerClassLoader.newInstance(args);
    }

    public static <T> T newProxy(ClassLoader loader, Class<T> inf, Function<ClassLoader, Object> provider) {
        return inf.cast(Proxy.newProxyInstance(loader, new Class<?>[]{inf}, new InvocationHandler() {
            private Object delegate;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (null == delegate) {
                    delegate = provider.apply(PROFILER_LOADER);
                }
                if (null == delegate) {
                    return null;
                }
                if (delegate.getClass().getClassLoader() instanceof ProfilerClassLoader) {
                    return MethodUtils.invokeMethod(delegate, true, method.getName(), transferParams(args));
                }
                return MethodUtils.invokeMethod(delegate, true, method.getName(), args);
            }
        }));
    }

    public static Enhancer newEnhancer() {
        return newProxy(Enhancer.class.getClassLoader(), Enhancer.class, loader -> {
            try {
                final Class<?> clazz = loader.loadClass("me.zyee.profiler.agent.core.enhancer.EventEnhancer");
                return clazz.newInstance();
            } catch (Throwable t) {
                return null;
            }
        });
    }

    public static Structure newStructure(final ClassLoader loader,
                                         final Class<?> classBeingRedefined,
                                         final byte[] srcByteCodeArray) {
        return AgentProxy.newProxy(Structure.class.getClassLoader(), Structure.class, profilerLoader -> {
            if (null == classBeingRedefined) {
                try {
                    final Class<?> factory = profilerLoader.loadClass("me.zyee.profiler.agent.core.utils.ClassStructureFactory");
                    final Object structure = MethodUtils.invokeStaticMethod(factory, "createClassStructure",
                            srcByteCodeArray, loader);

                    final Class<?> wrapper = profilerLoader.loadClass(StructureWrapper.class.getName());
                    return ConstructorUtils.invokeConstructor(wrapper, structure);
                } catch (Throwable e) {
                    return null;
                }
            } else {
                final ClassStructure classStructure = createClassStructure(classBeingRedefined);
                return new StructureWrapper(classStructure);
            }
        });

    }

    public static Hardware newHardware() {
        return newProxy(Hardware.class.getClassLoader(), Hardware.class, loader -> {
            try {
                final Class<?> hardware = loader.loadClass("me.zyee.profiler.agent.utils.OshiHardware");
                return hardware.newInstance();
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * todo 其他类型转换
     *
     * @param args
     * @return
     */
    private static Object[] transferParams(Object[] args) {
        if (null == args) {
            return null;
        }
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                result[i] = args[i];
            } else {
                final Class<?> clazz = args[i].getClass();
                final ClassLoader loader = clazz.getClassLoader();
                if (loader == null || loader instanceof ProfilerClassLoader) {
                    result[i] = args[i];
                } else if (clazz.isArray()) {
                    try {
                        final Class aClass = PROFILER_LOADER.loadClass(clazz.getComponentType().getName());
                        final int length = Array.getLength(args[i]);
                        final Object array = Array.newInstance(aClass, length);
                        for (int j = 0; j < length; j++) {
                            Array.set(array, j, Enum.valueOf(aClass, ((Enum) Array.get(args[i], j)).name()));
                        }
                        result[i] = array;
                    } catch (Exception e) {
                        result[i] = args[i];
                    }
                } else {
                    result[i] = args[i];
                }
            }
        }
        return result;
    }
}
