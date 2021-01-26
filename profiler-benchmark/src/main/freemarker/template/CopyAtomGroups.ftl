// DON'T MODIFY !
package me.zyee.java.profiler.benchmark.operation;
import static me.zyee.java.profiler.benchmark.CopyBenchmarks.ByteArrayCopyBenchmark;
import static me.zyee.java.profiler.benchmark.CopyBenchmarks.IntArrayCopyBenchmark;
import static me.zyee.java.profiler.benchmark.CopyBenchmarks.LongArrayCopyBenchmark;
import static me.zyee.java.profiler.benchmark.CopyBenchmarks.DoubleArrayCopyBenchmark;
import static me.zyee.java.profiler.benchmark.CopyBenchmarks.UnsafeCopyBenchmark;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import me.zyee.java.profiler.benchmark.BenchmarkInfo;
import me.zyee.java.profiler.benchmark.opertaion.CopyBenchmarkAtomGroup;
import me.zyee.java.profiler.operation.AtomGroup;
import me.zyee.java.profiler.operation.AtomGroupType;
import me.zyee.java.profiler.operation.AtomGroups;
import me.zyee.java.profiler.operation.CopyAtomGroup;
import me.zyee.java.profiler.operation.impl.DefaultAtomOperation;
import java.util.stream.Stream;
public class CopyAtomGroups {

<#list subClasses as subClass >
    <#assign item>${subClass.type?upper_case}_ARRAY</#assign>
    <#assign className>${subClass.type}<#if !subClass.unsafe>Array</#if>CopyAtomGroup</#assign>
    public static final CopyAtomGroup COPY_${item} = new ${className}();
</#list>

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




