package me.zyee.profiler.agent.core.utils;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import me.zyee.profiler.agent.loader.ProfilerClassLoader;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import static me.zyee.profiler.agent.core.utils.ClassStructureFactory.createClassStructure;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/19
 */
public class StructureProxy implements Structure {
    private final Object delegate;

    public StructureProxy(Object delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getJavaClassName() {
        try {
            return (String) MethodUtils.invokeMethod(delegate, "getJavaClassName");
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public Set<String> getMatchesBehaviors(BiPredicate<String, Stream<String>> predicate) {
        try {
            return (Set<String>) MethodUtils.invokeMethod(delegate, "getMatchesBehaviors", predicate);
        } catch (Throwable e) {
            return Collections.emptySet();
        }
    }

    public static Structure newInstance(final ClassLoader loader,
                                        final Class<?> classBeingRedefined,
                                        final byte[] srcByteCodeArray) {
        if (null == classBeingRedefined) {
            final ClassLoader profilerLoader = ProfilerClassLoader.getInstance();
            try {
                final Class<?> factory = profilerLoader.loadClass("me.zyee.profiler.agent.core.utils.ClassStructureFactory");
                final Object structure = MethodUtils.invokeStaticMethod(factory, "createClassStructure",
                        srcByteCodeArray, loader);

                final Class<?> wrapper = profilerLoader.loadClass(StructureWrapper.class.getName());
                final Object delegate = ConstructorUtils.invokeConstructor(wrapper, structure);
                return new StructureProxy(delegate);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            final ClassStructure classStructure = createClassStructure(srcByteCodeArray, loader);
            return new StructureProxy(classStructure);
        } else {
            final ClassStructure classStructure = createClassStructure(classBeingRedefined);
            return new StructureProxy(new StructureWrapper(classStructure));
        }
    }
}
