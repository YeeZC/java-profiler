package me.zyee.java.profiler.agent.operation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import me.zyee.java.profiler.operation.AtomOperation;
import me.zyee.java.profiler.operation.CopyAtomGroup;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/26
 */
public abstract class CopyBenchmarkAtomGroup extends BaseBenchmarkAtomGroup implements CopyAtomGroup {
    protected final Map<Long, AtomOperation> operations = new ConcurrentHashMap<>();

    public CopyBenchmarkAtomGroup(String name, String pattern, Class<?> benchmarkClass) {
        super(name, pattern, benchmarkClass);
    }

    @Override
    public Optional<AtomOperation> getAtomOperation(long count) {
        doRunBenchmarkIfNeed();
        if (operations.isEmpty()) {
            return Optional.empty();
        }
        if (operations.containsKey(count)) {
            return Optional.ofNullable(operations.get(count));
        }
        final long min = operations.keySet().stream().filter(key -> key >= count)
                .min(Comparator.naturalOrder()).orElse(-1L);
        if (min == -1) {
            return Optional.empty();
        }
        return Optional.ofNullable(operations.get(min));
    }

    @Override
    public List<AtomOperation> getAllOperations() {
        return operations.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
