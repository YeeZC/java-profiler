package me.zyee.java.profiler.theoretical.function;

import me.zyee.java.profiler.theoretical.function.annotation.MainMethod;
import me.zyee.java.profiler.theoretical.function.annotation.Meta;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * 自定义方法注册器
 *
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public interface FunctionRegistry {
    /**
     * 注册方法
     *
     * @param function
     * @return
     */
    FunctionRegistry register(Function<?> function);

    /**
     * 注册方法
     *
     * @param function
     * @return
     */
    default FunctionRegistry register(Class<?> function) {
        final int modifiers = function.getModifiers();
        if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
            throw new IllegalArgumentException();
        }
        if (function.isAnnotationPresent(Meta.class)) {
            final Meta meta = function.getAnnotation(Meta.class);
            final List<Method> methods = MethodUtils
                    .getMethodsListWithAnnotation(function, MainMethod.class, false, true);
            if (methods.isEmpty()) {
                throw new IllegalArgumentException();
            }
            Method method = methods.get(0);
            try {
                Object obj = function.newInstance();
                return register(new Function<Object>() {
                    @Override
                    public int getParameterCount() {
                        return method.getParameterCount();
                    }

                    @Override
                    public String getName() {
                        return meta.name();
                    }

                    @Override
                    public Object eval(Object... args) {
                        try {
                            return method.invoke(obj, args);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
            } catch (IllegalAccessException | InstantiationException e) {
                throw new IllegalArgumentException(e);
            }

        } else if (ClassUtils.isAssignable(function, Function.class)
                || ClassUtils.isAssignable(Function.class, function)) {
            try {
                return register((Function<?>) function.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                throw new IllegalArgumentException(e);
            }
        }
        throw new IllegalArgumentException(function.getName());
    }
}
