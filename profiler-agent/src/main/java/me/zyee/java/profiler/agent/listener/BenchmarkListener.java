package me.zyee.java.profiler.agent.listener;

import me.zyee.java.profiler.benchmark.BenchmarkInfo;
import me.zyee.java.profiler.event.Before;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.Return;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.operation.BenchmarkAtomOperation;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.format.OutputFormatFactory;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/4/2
 */
public class BenchmarkListener implements EventListener {
    private BenchmarkAtomOperation operation;

    @Override
    public boolean onEvent(Event event) throws Throwable {
        switch (event.type()) {
            case BEFORE:
                this.operation = (BenchmarkAtomOperation) ((Before) event).getTrigger();
                break;
            case RETURN:
                final Boolean returnObject = (Boolean) ((Return) event).getReturnObject();
                if (returnObject && null != operation) {
                    System.out.println("Run Benchmark " + this.operation.getBenchmarkClass().getSimpleName());
                    try {
                        final Optional<BenchmarkInfo> benchmarkInfo = run();
                        benchmarkInfo.ifPresent(info -> {
                            long cost;
                            switch (info.getMode(Mode.class)) {
                                case Throughput:
                                    cost = (long) (info.getUnit().toMillis(1) * (10000000L * (1 / info.getScore())));
                                    break;
                                case AverageTime:
                                    cost = (long) (info.getUnit().toMillis(1) * info.getScore());
                                    break;
                                default:
                                    cost = 0;
                                    break;
                            }
                            System.out.printf("benchmark %s score %d ms/1kw \n", this.operation.getBenchmarkClass().getSimpleName(), cost);
                            operation.setCost(cost);
                        });

                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            default:
        }
        return false;
    }

    private Optional<BenchmarkInfo> run() throws RunnerException {
        final OptionsBuilder build = new OptionsBuilder();
        build.include(operation.getBenchmarkClass().getSimpleName());
        final Collection<RunResult> run =
                new Runner(build, OutputFormatFactory.createFormatInstance(System.out, VerboseMode.SILENT)).run();
        return run.stream().map(result -> {
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
            return builder.build();
        }).findFirst();

    }
}
