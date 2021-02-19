package me.zyee.java.profiler.benchmark;

import java.util.Collection;
import java.util.stream.Collectors;
import me.zyee.java.profiler.utils.StringHelper;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.format.OutputFormatFactory;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/21
 */
public class Main {
    public static void main(String[] args) throws RunnerException {
        final OptionsBuilder build = new OptionsBuilder();
        for (String arg : args) {
            build.include(arg);
        }
        final Collection<RunResult> run =
                new Runner(build, OutputFormatFactory.createFormatInstance(System.out, VerboseMode.SILENT)).run();
        run.stream().map(result -> {
            final BenchmarkInfo.Builder builder = BenchmarkInfo.builder();
            final BenchmarkParams params = result.getParams();
            builder.setParams(params.getParamsKeys().stream().map(key -> {
                BenchmarkInfo.Param param = new BenchmarkInfo.Param();
                param.name = key;
                param.value = params.getParam(key);
                return param;
            }).collect(Collectors.toList()));
            builder.setMode(params.getMode().name());
            final Result pres = result.getPrimaryResult();
            builder.setCount(pres.getSampleCount());
            builder.setScore(pres.getScore());
            builder.setError(pres.getScoreError());
            builder.setUnit(params.getTimeUnit());
            return StringHelper.toString(builder.build());
        }).forEach(System.out::println);
    }
}
