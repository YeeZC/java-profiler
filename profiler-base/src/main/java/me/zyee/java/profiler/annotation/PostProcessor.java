package me.zyee.java.profiler.annotation;

/**
 * 对profile方法的增强处理器
 *
 * @author yee
 * @version 1.0
 * Create by yee on 2021/2/1
 */
public interface PostProcessor {
    /**
     * 在执行profile方法之前需要执行的方法
     *
     * @param args profile方法的参数
     */
    void before(Object... args);

    /**
     * 执行profile方法之后需要执行的方法
     *
     * @param value profile方法执行的返回值
     */
    void after(Object value);
}
