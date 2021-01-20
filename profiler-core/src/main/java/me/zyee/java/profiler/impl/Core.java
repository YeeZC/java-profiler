package me.zyee.java.profiler.impl;

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
import me.zyee.java.profiler.AtomOperation;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.NormalOperation;
import me.zyee.java.profiler.Operation;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.ProfilerCore;
import me.zyee.java.profiler.Result;
import me.zyee.java.profiler.Runner;
import me.zyee.java.profiler.attach.Attach;
import me.zyee.java.profiler.flame.FlameParser;
import me.zyee.java.profiler.flame.Frame;
import me.zyee.java.profiler.markdown.Markdown;
import me.zyee.java.profiler.module.CoreModule;
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

    private Core(Builder builder) {
        this.reportPath = builder.reportPath;
        this.excludes = builder.excludes;
        this.dumpClassFile = Optional.ofNullable(builder.dumpClassFile)
                .orElse(false);
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

        final Result apply = runner.apply(context);
        if (apply.isOk()) {
            final Queue<ProfileItem> items = context.getProfileItems();
            Map<String, Long> theoreticalCost = new HashMap<>();
            while (items.peek() != null) {
                final ProfileItem item = items.poll();
                final Path flamePath = item.getFlamePath();
                final Queue<Operation> nodes = item.getNodes();
                ProfileNode root = new ProfileNode();
                root.setName(item.getProfileName() + " Report");
                final Set<String> patterns = new HashSet<>();
                while (null != nodes.peek()) {
                    ProfileNode child = new ProfileNode();
                    final Operation node = nodes.poll();
                    makeProfileNode(patterns, child, node);
                    root.addChild(child);
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
                            final List<Matcher<String>> matchers = classes.stream().map(clazz -> clazz.getName() + "." + target)
                                    .map((Function<String, Matcher<String>>) Matcher::classNameMatcher).collect(Collectors.toList());
                            return new GroupMatcher.Or<>(matchers);
                        }));
                root.merge();
                final Map<String, Frame> parse = FlameParser.parse(flamePath, root, patternMap);
                Markdown profileResult = new Markdown(item.getCost(), root,
                        Optional.ofNullable(reportPath).orElse(
                                Paths.get(System.getProperty("user.dir"))).resolve(item.getProfileName() + ".md"));
                profileResult.setFrames(parse);
                profileResult.setTheoreticalCost(theoreticalCost);
                profileResult.output();
            }
        }
    }

    private void makeProfileNode(Set<String> patterns, ProfileNode child, Operation node) {
        child.setName(node.getName());
        child.setPattern(node.getPattern());
        child.setChildren(new ArrayList<>());
        patterns.addAll(getPatterns(node, child));
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
                ProfileNode child = new ProfileNode();
                child.setName(n.getName());
                child.setPattern(n.getPattern());
                child.setChildren(new ArrayList<>());
                if (n instanceof AtomOperation) {
                    child.setAtom((double) n.getCost());
                }
                profileNode.addChild(child);
                return getPatterns(n, child);
            }).forEach(patterns::addAll);
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

        public Builder of(Core core) {
            this.reportPath = core.reportPath;
            this.excludes = core.excludes;
            this.dumpClassFile = core.dumpClassFile;
            return this;
        }

        public Core build() {
            return new Core(this);
        }
    }
}
