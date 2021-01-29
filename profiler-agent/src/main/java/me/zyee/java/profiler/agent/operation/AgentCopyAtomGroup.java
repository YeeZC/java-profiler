package me.zyee.java.profiler.agent.operation;

import java.util.List;
import java.util.Optional;
import me.zyee.java.profiler.operation.AtomOperation;
import me.zyee.java.profiler.operation.CopyAtomGroup;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/29
 */
public interface AgentCopyAtomGroup extends CopyAtomGroup {
    @Override
    @MethodProxy(OptionAtomMethodProcess.class)
    Optional<AtomOperation> getAtomOperation(long count);

    @Override
    @MethodProxy(AtomListMethodProcess.class)
    List<AtomOperation> getAllOperations();
}
