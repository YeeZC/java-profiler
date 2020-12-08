package me.zyee.java.profiler.annotation;

import me.zyee.java.profiler.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Atoms {
    Atom[] value();

    @interface Atom {
        Class<? extends Operation> operation();

        int when();

        int expect();

        String pattern() default "";

        long cost() default 0L;

        String name() default "";
    }
}
