package me.zyee.java.profiler.benchmark;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.TearDown;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class CopyBenchmarks {
<#list subClasses as subClass >
    <#assign className>${subClass.type}<#if !subClass.unsafe>Array</#if>CopyBenchmark</#assign>
    <#assign typeName><#if subClass.unsafe>byte<#else >${subClass.type?lower_case}</#if></#assign>
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @State(Scope.Benchmark)
    @Warmup(iterations = 0)
    @Fork(1)
    @Measurement(iterations = 1, time = 5)
    public static class ${className} {
        @Param({"10"})
        private int length;

        private ${typeName}[] data;
        private int start;

        <#if subClass.unsafe>
            private static final Unsafe UNSAFE;

            static {
                Unsafe unsafe;
                try {
                    Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                    unsafeField.setAccessible(true);
                    unsafe = (Unsafe) unsafeField.get(null);
                } catch (Throwable cause) {
                    unsafe = null;
                }
                UNSAFE = unsafe;
            }
        </#if>

        @Setup(Level.Invocation)
        public void init() {
            Random random = new Random();
            data = new ${typeName}[random.nextInt(length) + 1];
            <#if subClass.bytes>
            random.nextBytes(data);
            <#else >
            for (int i = 0; i < data.length; i++) {
                data[i] = random.next${subClass.type}();
            }
            </#if>

            start = random.nextInt(data.length);
        }
        @Benchmark
        public ${typeName}[] test() {
            ${typeName}[] result = new ${typeName}[data.length * 2];
            <#if !subClass.unsafe>
            System.arraycopy(data, 0, result, start, data.length);
            <#else >
            UNSAFE.copyMemory(data, Unsafe.ARRAY_BYTE_BASE_OFFSET, result, Unsafe.ARRAY_BYTE_BASE_OFFSET + start, data.length);
            </#if>
            return result;
        }

        @TearDown(Level.Invocation)
        public void destroy() {
            data = null;
        }
    }
</#list>
}