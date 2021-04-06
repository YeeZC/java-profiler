package me.zyee.java.profiler;

import java.util.Queue;

/**
 * 用于计算耗时的参照，产生实际Profile的步骤
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
