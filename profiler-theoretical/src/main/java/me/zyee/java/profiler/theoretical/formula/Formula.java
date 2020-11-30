package me.zyee.java.profiler.theoretical.formula;

/**
 * 公式
 *
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public interface Formula<T> {
    /**
     * 执行方法
     *
     * @param args
     * @return
     */
    T eval(Object... args);
}
