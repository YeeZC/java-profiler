package me.zyee.java.profiler.operation;

import java.util.List;
import me.zyee.java.profiler.Operation;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/26
 */
public interface AtomGroup extends Operation {
    List<AtomOperation> getAllOperations();

    @Override
    default long getCost() {
        return 0L;
    }
}
