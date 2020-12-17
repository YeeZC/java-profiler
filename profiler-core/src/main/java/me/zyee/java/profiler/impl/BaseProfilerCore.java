package me.zyee.java.profiler.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.zyee.java.profiler.ProfilerCore;
import me.zyee.java.profiler.Runner;
import me.zyee.java.profiler.Task;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public abstract class BaseProfilerCore implements ProfilerCore {
    private final List<Task> before = new ArrayList<>();
    private final List<Task> after = new ArrayList<>();
    private final List<Task> failed = new ArrayList<>();
    private final List<Task> finished = new ArrayList<>();

    @Override
    public void profile(Runner runner) throws IOException {
//        Context context = ContextHelper.newContext(runner.name(), Events.CPU);
//        for (Task task : before) {
//            final Result result = task.apply(context);
//            if (!result.isOk()) {
//                throw new RuntimeException(result.getThrowable());
//            }
//        }
//        final Result result = runner.apply(context);
//        if (result.isOk()) {
//            for (Task task : after) {
//                task.apply(context);
//            }
//            final Queue<ProfileItem> profileItems = context.getProfileItems();
//            final List<Class<?>> allLoadedClasses = Optional.ofNullable(MethodAgent.inst)
//                    .map(inst -> (List<Class<?>>) Lists.<Class<?>>newArrayList(inst.getAllLoadedClasses()))
//                    .orElseGet(Collections::emptyList);
//            while (profileItems.peek() != null) {
//                final ProfileItem item = profileItems.poll();
//                if (item.getThrowable() == null) {
//                    final Path flamePath = item.getFlamePath();
//                    final Atoms atoms = item.getAtoms();
//                    Map<String, GroupMatcher.Or<String>> patternMap = new HashMap<>();
//
//                    final Map<String, Set<Class<?>>> collect = Arrays.stream(atoms.value()).map(Atoms.Atom::pattern).map(pattern -> StringUtils.substringBefore(pattern, "#"))
//                            .distinct().collect(Collectors.toMap(classPattern -> classPattern, classPattern ->
//                                    SearchUtils.searchClass(allLoadedClasses::stream, classPattern, false)));
//                    Arrays.stream(atoms.value()).map(Atoms.Atom::pattern)
//                            .forEach(pattern -> {
//                                final String[] split = pattern.split("#");
//                                final Set<Class<?>> set = collect.getOrDefault(split[0], Collections.emptySet());
//                                final GroupMatcher.Or<String> value = new GroupMatcher.Or<>();
//                                value.add(SearchUtils.classNameMatcher(pattern));
//                                patternMap.put(pattern, value);
//                                for (Class<?> clazz : set) {
//                                    patternMap.get(pattern).add(SearchUtils.classNameMatcher(clazz.getName() + "." + split[1]));
//                                }
//                            });
//                    ProfileNode root = new ProfileNode();
//                    for (Atoms.Atom atom : atoms.value()) {
//                        final String name = atom.name();
//                        final String pattern = atom.pattern();
//                        final long cost = atom.cost();
//                        final ProfileNode node = new ProfileNode();
//                        node.setAtom((double) cost);
//                        node.setPattern(pattern);
//                        node.setName(name);
//                    }
//                    root.merge();
//                    final Map<String, Frame> parse = FlameParser.parse(flamePath, root, patternMap);
//                    MarkdownProfileResult profileResult = new MarkdownProfileResult(item.getCost(), root,
//                            Paths.get(System.getProperty("user.dir"), item.getProfileName() + ".md"));
//                    profileResult.setFrames(parse);
//                    profileResult.setTheoreticalCost(theoreticalCost);
//                }
//            }
//            for (Task task : finished) {
//                task.apply(context);
//            }
//        } else {
//            for (Task task : failed) {
//                task.apply(context);
//            }
//        }
    }

}
