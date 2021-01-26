package me.zyee.java.profiler.operation;

import java.util.Optional;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/26
 */
public interface CopyAtomGroup extends AtomGroup {
    Optional<AtomOperation> getAtomOperation(long count);
}
