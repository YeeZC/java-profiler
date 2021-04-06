package me.zyee.java.profiler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Profile注解，将注解打到需要Profile的方法上
 * 工具自动识别
 *
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Profile {
    /**
     * 需要算详细执行次数的表达式
     *
     * @return
     */
    String[] counters() default {};

    /**
     * profile方法的增强处理器
     *
     * @return
     */
    Class<? extends PostProcessor>[] processors() default {};
}
