// DON'T MODIFY !
package me.zyee.java.profiler.agent.operation;
import static me.zyee.java.profiler.agent.benchmark.CopyBenchmarks.ByteArrayCopyBenchmark;
import static me.zyee.java.profiler.agent.benchmark.CopyBenchmarks.IntArrayCopyBenchmark;
import static me.zyee.java.profiler.agent.benchmark.CopyBenchmarks.LongArrayCopyBenchmark;
import static me.zyee.java.profiler.agent.benchmark.CopyBenchmarks.DoubleArrayCopyBenchmark;
import static me.zyee.java.profiler.agent.benchmark.CopyBenchmarks.UnsafeCopyBenchmark;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import me.zyee.java.profiler.agent.benchmark.BenchmarkInfo;
import me.zyee.java.profiler.agent.utils.Initializer;
import me.zyee.java.profiler.operation.AtomGroup;
import me.zyee.java.profiler.operation.AtomGroupType;
import me.zyee.java.profiler.operation.AtomGroups;
import me.zyee.java.profiler.operation.CopyAtomGroup;
import me.zyee.java.profiler.operation.impl.DefaultAtomOperation;
import java.util.stream.Stream;
import org.apache.commons.lang3.reflect.MethodUtils;
public class CopyAtomGroups {

    public static void register(ClassLoader loader) {
        <#list subClasses as subClass >
            <#assign item>${subClass.type?upper_case}_ARRAY</#assign>
            <#assign className>${subClass.type}<#if !subClass.unsafe>Array</#if>CopyAtomGroup</#assign>
        create(loader, "${className}").ifPresent(group -> AtomGroups.register(AtomGroupType.COPY_${item}, group));
        </#list>
    }

    private static Optional<CopyAtomGroup> create(ClassLoader loader, String className) {
        try {
            Class<?> clazz = loader.loadClass(String.format("%s$%s", CopyAtomGroups.class.getName(), className));
            Object o = clazz.newInstance();
            return Optional.of((CopyAtomGroup) Proxy.newProxyInstance(CopyAtomGroup.class.getClassLoader(), new Class[] {CopyAtomGroup.class},
                (proxy, method, args) -> MethodUtils.invokeMethod(o, true, method.getName(), args)));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return Optional.empty();
    }


<#list subClasses as subClass >
<#assign className>${subClass.type}<#if !subClass.unsafe>Array</#if>CopyAtomGroup</#assign>
<#assign benchmark>${subClass.type}<#if !subClass.unsafe>Array</#if>CopyBenchmark.class</#assign>
<#assign pattern><#if !subClass.unsafe>j</#if>${subClass.type?lower_case}*arraycopy</#assign>
<#assign name>${subClass.type}数组拷贝</#assign>

    static class ${className} extends CopyBenchmarkAtomGroup {
        ${className}() {
            super("${name}", "${pattern}", ${benchmark});
        }

        @Override
        protected void build(Stream<BenchmarkInfo> stream) {
            stream.forEach(info -> {
                BenchmarkInfo.Param param = info.getParams().get(0);
                long value = Long.valueOf(param.value);
                long cost = 0L;
                switch (info.getMode()) {
                    case Throughput:
                        cost = (long)(info.getUnit().toMillis(1) * (10000000L * (1 / info.getScore())));
                        break;
                    case AverageTime:
                        cost = (long) (info.getUnit().toMillis(1) * info.getScore());
                        break;
                }
                operations.put(value, DefaultAtomOperation.builder()
                    .setName(String.format("${name} (%d)", value))
                    .setPattern("${pattern}")
                    .setCost(cost)
                    .build());
            });
        }
    }
            </#list>
}




