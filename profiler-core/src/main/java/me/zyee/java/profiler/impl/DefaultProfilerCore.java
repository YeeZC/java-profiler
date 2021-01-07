package me.zyee.java.profiler.impl;

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
import me.zyee.java.profiler.event.MethodProfileListener;
import me.zyee.java.profiler.filter.ProfileBehaviorFilter;
import me.zyee.java.profiler.flame.FlameParser;
import me.zyee.java.profiler.flame.Frame;
import me.zyee.java.profiler.markdown.Markdown;
import me.zyee.java.profiler.utils.GroupMatcher;
import me.zyee.java.profiler.utils.PidUtils;
import me.zyee.java.profiler.utils.ProfilerHelper;
import me.zyee.java.profiler.utils.SearchUtils;
import one.profiler.Events;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * TODO 实现
 *
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/15
 */
public class DefaultProfilerCore implements ProfilerCore {
    static {
        try {
            Attach.attach(Paths.get("/Users/yee/IdeaProjects/java-profiler/profiler-agent/target/profiler-agent-1.0-SNAPSHOT-jar-with-dependencies.jar"),
                    PidUtils.currentPid());
            ProfilerHelper.watcher.watch(new ProfileBehaviorFilter(), new MethodProfileListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void profile(Runner runner) throws IOException {
        final Context context = ContextHelper.newContext(runner.name(), Events.CPU);
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
                final Map<String, GroupMatcher.Or<String>> patternMap = new HashMap<>(0);
                ProfileNode root = new ProfileNode();
                root.setName(item.getProfileName() + " Report");
                final Set<String> patterns = new HashSet<>();
                while (null != nodes.peek()) {
                    ProfileNode child = new ProfileNode();
                    final Operation node = nodes.poll();
                    makeProfileNode(patterns, child, node);
                    root.addChild(child);
                    calculateTheoreticalCost(theoreticalCost, node);
                }
                final Map<String, Set<Class<?>>> collect = patterns.stream()
                        .map(pattern -> StringUtils.substringBefore(pattern, "#"))
                        .distinct().collect(Collectors.toMap(classPattern -> classPattern, ProfilerHelper::find));
                for (String pattern : patterns) {
                    final String[] split = pattern.split("#");
                    final Set<Class<?>> set = collect.getOrDefault(split[0], Collections.emptySet());
                    final GroupMatcher.Or<String> value = new GroupMatcher.Or<>();
                    value.add(SearchUtils.classNameMatcher(pattern));
                    patternMap.put(pattern, value);
                    for (Class<?> clazz : set) {
                        patternMap.get(pattern).add(SearchUtils.classNameMatcher(clazz.getName() + "." + split[1]));
                    }
                }
                root.merge();
                final Map<String, Frame> parse = FlameParser.parse(flamePath, root, patternMap);
                Markdown profileResult = new Markdown(item.getCost(), root,
                        Paths.get(System.getProperty("user.dir")).resolve(item.getProfileName() + ".md"));
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

    private void calculateTheoreticalCost(Map<String, Long> theoreticalCost, Operation node) {
        if (node instanceof AtomOperation) {
            final long cost = node.getCost();
            final long expect = ((AtomOperation) node).getExpect();
            final long when = ((AtomOperation) node).getWhen();
            final Supplier<Long> actual = ((AtomOperation) node).getActual();

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
                calculateTheoreticalCost(theoreticalCost, opChild);
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
}
