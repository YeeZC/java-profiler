package me.zyee.java.profiler;

import java.util.Queue;

/**
 * 用于计算耗时的参照，如实际数据量
 *
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/16
 */
public interface ProfileHandler {
    /**
     * 获取OperationQueue
     *
     * @return
     */
    Queue<Operation> next();
}
