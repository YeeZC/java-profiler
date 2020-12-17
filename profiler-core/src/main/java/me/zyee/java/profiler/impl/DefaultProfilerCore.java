package me.zyee.java.profiler.impl;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.MarkdownProfileResult;
import me.zyee.java.profiler.NormalOperation;
import me.zyee.java.profiler.Operation;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.ProfilerCore;
import me.zyee.java.profiler.Result;
import me.zyee.java.profiler.Runner;
import me.zyee.java.profiler.agent.MethodAgent;
import me.zyee.java.profiler.flame.FlameParser;
import me.zyee.java.profiler.flame.Frame;
import me.zyee.java.profiler.utils.GroupMatcher;
import me.zyee.java.profiler.utils.SearchUtils;
import one.profiler.Events;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO 实现
 *
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/15
 */
public class DefaultProfilerCore implements ProfilerCore {
    @Override
    public void profile(Runner runner) throws IOException {
        final Context context = ContextHelper.newContext(runner.name(), Events.CPU);
        if (null == context) {
            throw new UnsupportedOperationException();
        }

        final Result apply = runner.apply(context);
        if (apply.isOk()) {
            final Queue<ProfileItem> items = context.getProfileItems();
            while (items.peek() != null) {
                final ProfileItem item = items.poll();
                final Path flamePath = item.getFlamePath();
                final Queue<Operation> nodes = item.getNodes();
                final Map<String, GroupMatcher.Or<String>> patternMap = new HashMap<>(0);
                ProfileNode root = new ProfileNode();
                final Set<String> patterns = new HashSet<>();
                while (null != nodes.peek()) {
                    ProfileNode child = new ProfileNode();
                    final Operation node = nodes.poll();
                    child.setName(node.getName());
                    child.setPattern(node.getPattern());
                    child.setChildren(new ArrayList<>());
                    root.addChild(child);
                    patterns.addAll(getPatterns(node, child));
//                    calculateTheoreticalCost(node, theoreticalCost, names);
                }
                final List<Class<?>> allLoadedClasses = Optional.ofNullable(MethodAgent.inst)
                        .map(inst -> (List<Class<?>>) Lists.<Class<?>>newArrayList(inst.getAllLoadedClasses()))
                        .orElseGet(Collections::emptyList);
                final Map<String, Set<Class<?>>> collect = patterns.stream().map(pattern -> StringUtils.substringBefore(pattern, "#"))
                        .distinct().collect(Collectors.toMap(classPattern -> classPattern, classPattern ->
                                SearchUtils.searchClass(allLoadedClasses::stream, classPattern, false)));
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
                MarkdownProfileResult profileResult = new MarkdownProfileResult(item.getCost(), root,
                        Paths.get(System.getProperty("user.dir")).resolve(item.getProfileName() + ".md"));
                profileResult.setFrames(parse);
//                profileResult.setTheoreticalCost(theoreticalCost);
//                return profileResult;
//                parse.forEach((k, v) -> {
//                    v.setName(names.get(k));
//                });
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
//                child.setAtom(n.getAtomCost());
                profileNode.addChild(child);
                return getPatterns(n, child);
            }).forEach(patterns::addAll);
        }
        return patterns;
    }
}
