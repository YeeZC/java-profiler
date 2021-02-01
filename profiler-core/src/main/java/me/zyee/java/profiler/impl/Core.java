package me.zyee.java.profiler.impl;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.Operation;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.ProfilerCore;
import me.zyee.java.profiler.Result;
import me.zyee.java.profiler.Runner;
import me.zyee.java.profiler.attach.Attach;
import me.zyee.java.profiler.flame.FlameParser;
import me.zyee.java.profiler.module.CoreModule;
import me.zyee.java.profiler.operation.AtomGroup;
import me.zyee.java.profiler.operation.AtomOperation;
import me.zyee.java.profiler.operation.NormalOperation;
import me.zyee.java.profiler.operation.impl.DefaultAtomOperation;
import me.zyee.java.profiler.operation.impl.DefaultOperation;
import me.zyee.java.profiler.report.Report;
import me.zyee.java.profiler.report.markdown.Title;
import me.zyee.java.profiler.report.plugin.AtomPlugin;
import me.zyee.java.profiler.report.plugin.ConclusionPlugin;
import me.zyee.java.profiler.report.plugin.StepPlugin;
import me.zyee.java.profiler.utils.GroupMatcher;
import me.zyee.java.profiler.utils.Matcher;
import one.profiler.Events;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/15
 */
public class Core implements ProfilerCore {
    private final Path reportPath;
    private final Set<String> excludes;
    private final boolean dumpClassFile;
    private final int warmups;
    private final double collectMinPercent;

    private Core(Builder builder) {
        this.reportPath = builder.reportPath;
        this.excludes = builder.excludes;
        this.dumpClassFile = Optional.ofNullable(builder.dumpClassFile)
                .orElse(false);
        this.warmups = Optional.ofNullable(builder.warmups).orElse(0);
        this.collectMinPercent = builder.collectMinPercent;
        try {
            Attach.attach(this.dumpClassFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void profile(Runner runner) throws IOException {
        final Context context = ContextHelper.newContext(runner.name(), Events.CPU, excludes);
        if (null == context) {
            throw new UnsupportedOperationException();
        }

        if (CoreModule.entryWarmup()) {
            for (int i = 0; i < warmups; i++) {
                runner.apply(context);
            }
            CoreModule.exitWarmup();
        }

        final Result apply = runner.apply(context);
        if (apply.isOk()) {
            final Queue<ProfileItem> items = context.getProfileItems();
            Map<String, Long> theoreticalCost = new HashMap<>();
            while (items.peek() != null) {
                final ProfileItem item = items.poll();
                if (item.getThrowable() != null) {
                    continue;
                }
                final Path flamePath = item.getFlamePath();
                final Queue<Operation> nodes = item.getNodes();
                ProfileNode root = new ProfileNode();
                final Set<String> patterns = new HashSet<>();
                while (null != nodes.peek()) {
                    final Operation node = nodes.poll();
                    makeProfileNode(patterns, node).forEach(root::addChild);
                    calculateTheoreticalCost(item, theoreticalCost, node);
                }

                final Map<String, Matcher<String>> patternMap = patterns.stream()
                        .collect(Collectors.toMap(pattern -> pattern, pattern -> {
                            String[] split = pattern.split("#");
                            final Set<Class<?>> classes = CoreModule.find(split[0]);
                            String methodPattern = "*";
                            if (split.length >= 2) {
                                methodPattern = split[1];
                            }

                            String target = methodPattern;
                            if (!classes.isEmpty()) {
                                final List<Matcher<String>> matchers = classes.stream().map(clazz -> clazz.getName() + "." + target)
                                        .map((Function<String, Matcher<String>>) Matcher::classNameMatcher).collect(Collectors.toList());
                                return new GroupMatcher.Or<>(matchers);
                            }
                            return Matcher.classNameMatcher(pattern);
                        }));
                root.merge();
                Set<String> warnings = new HashSet<>();
                Set<String> errors = new HashSet<>();
                Report.builder().setTitle(Title.builder().setTitle(item.getProfileName()).build())
                        .addContents(new AtomPlugin(root),
                                StepPlugin.builder(root, warnings, errors).setCost(item.getCost())
                                        .setTheoreticalCost(theoreticalCost)
                                        .setFrames(() -> FlameParser.parse(flamePath, root, patternMap, collectMinPercent)).build(), new ConclusionPlugin(warnings, errors))
                        .build().output(Optional.ofNullable(reportPath).orElse(
                        Paths.get(System.getProperty("user.dir"))).resolve(runner.name() + item.getProfileName() + ".md"));
            }
        }
    }

    private List<ProfileNode> makeProfileNode(Set<String> patterns, Operation node) {
        List<ProfileNode> result = new ArrayList<>();
        if (node instanceof AtomGroup) {
            ((AtomGroup) node).getAllOperations().stream().map(op -> makeProfileNode(patterns, op)).forEach(result::addAll);
        } else {
            ProfileNode child = new ProfileNode();
            child.setName(node.getName());
            child.setPattern(node.getPattern());
            child.setChildren(new ArrayList<>());
            patterns.addAll(getPatterns(node, child));
            result.add(child);
        }
        return result;
    }

    private void calculateTheoreticalCost(ProfileItem item, Map<String, Long> theoreticalCost, Operation node) {
        if (node instanceof AtomOperation) {
            final long cost = node.getCost();
            final long expect = ((AtomOperation) node).getExpect();
            final long when = ((AtomOperation) node).getWhen();
            final Supplier<Long> actual = Optional.ofNullable(item.getActualCost().get(node.getPattern()))
                    .orElse(((AtomOperation) node).getActual());

            theoreticalCost.compute(node.getPattern(), (key, before) -> {
                final long eval = expect * actual.get() * cost / when / 10000000;
                if (null != before) {
                    return before + eval;
                }
                return eval;
            });
        } else if (node instanceof AtomGroup) {
            ((AtomGroup) node).getAllOperations().forEach(op -> calculateTheoreticalCost(item, theoreticalCost, op));
        } else {
            NormalOperation op = (NormalOperation) node;
            for (Operation opChild : op.getChildren()) {
                calculateTheoreticalCost(item, theoreticalCost, opChild);
            }
        }

    }

    private Set<String> getPatterns(Operation node, ProfileNode profileNode) {
        Set<String> patterns = new HashSet<>();
        final String pattern = node.getPattern();
        if (StringUtils.isNotEmpty(pattern)) {
            patterns.add(pattern);
        }
        if (node instanceof NormalOperation) {
            ((NormalOperation) node).getChildren().stream().map(n -> {
                if (n instanceof AtomGroup) {
                    ((AtomGroup) n).getAllOperations().forEach(op -> {
                        ProfileNode child = new ProfileNode();
                        child.setName(op.getName());
                        child.setPattern(op.getPattern());
                        child.setAtom((double) op.getCost());
                        profileNode.addChild(child);
                    });
                    return Sets.newHashSet(n.getPattern());
                } else {
                    ProfileNode child = new ProfileNode();
                    child.setName(n.getName());
                    child.setPattern(n.getPattern());
                    child.setChildren(new ArrayList<>());
                    if (n instanceof AtomOperation) {
                        child.setAtom((double) n.getCost());
                    }
                    profileNode.addChild(child);
                    return getPatterns(n, child);
                }
            }).forEach(patterns::addAll);
        } else if (node instanceof AtomOperation) {
            profileNode.setAtom((double)node.getCost());
        }
        return patterns;
    }

    public Path getReportPath() {
        return reportPath;
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public boolean isDumpClassFile() {
        return dumpClassFile;
    }

    public static class Builder {
        private Path reportPath;
        private Set<String> excludes;
        private Boolean dumpClassFile;
        private Integer warmups;
        private Double collectMinPercent = .5D;

        private Builder() {
            this.excludes = new HashSet<>();
            this.excludes.add("*Java: C*,CompileBroker");
        }

        public Builder setReportPath(Path reportPath) {
            this.reportPath = reportPath;
            return this;
        }

        public Builder exclude(String exclude) {
            this.excludes.add(exclude);
            return this;
        }

        public Builder setDumpClassFile(boolean dumpClassFile) {
            this.dumpClassFile = dumpClassFile;
            return this;
        }

        public Builder setWarmups(int warmups) {
            this.warmups = warmups;
            return this;
        }

        public Builder setCollectMinPercent(double collectMinPercent) {
            this.collectMinPercent = collectMinPercent;
            return this;
        }

        public Builder of(Core core) {
            this.reportPath = core.reportPath;
            this.excludes = core.excludes;
            this.dumpClassFile = core.dumpClassFile;
            this.warmups = core.warmups;
            this.collectMinPercent = core.collectMinPercent;
            return this;
        }

        public Core build() {
            return new Core(this);
        }
    }
}
