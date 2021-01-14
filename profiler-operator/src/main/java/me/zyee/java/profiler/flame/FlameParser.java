package me.zyee.java.profiler.flame;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.fork.ForkJoiner;
import me.zyee.java.profiler.utils.Matcher;
import me.zyee.java.profiler.utils.OS;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/8/6
 */
public class FlameParser {
    public static Map<String, Frame> parse(Path path, ProfileNode node, Map<String, Matcher<String>> patterns) throws IOException {
        if (OS.getOSType() == OS.OSType.Windows) {
            throw new UnsupportedOperationException();
        }
        final Document parse = Jsoup.parse(path.toFile(), "UTF-8");
        final Elements select = parse.select("ul.tree>li");
        final FlameNode simple = ForkJoiner.invoke(new FlameNodeTask(1, select,
                span -> patterns.values().stream().anyMatch(matcher -> matcher.matching(span))));
        final Frame root = new Frame();
        root.setName("Profile");

        ProfileNode simple1 = simple(node);
        return build(simple1, simple, patterns);
    }

    private static List<FlameNode> findMatched(FlameNode current, Matcher<String> matcher) {
        List<FlameNode> result = new ArrayList<>();
        if (matcher.matching(current.getName())) {
            result.add(current);
        } else {
            for (FlameNode child : current.getChildren()) {
                result.addAll(findMatched(child, matcher));
            }
        }
        return result;
    }

    private static Map<String, Frame> build(ProfileNode node, FlameNode flameNode, Map<String, Matcher<String>> patterns) {
        Map<String, Frame> result = new HashMap<>(0);
        final String pattern = node.getPattern();
        if (StringUtils.isNotEmpty(pattern)) {
            final Matcher<String> matcher = patterns.get(pattern);
            final List<FlameNode> matched = findMatched(flameNode, matcher);
            if (!matched.isEmpty()) {
                for (FlameNode c : matched) {
                    result.compute(pattern, (k, f) -> {
                        Frame target = new Frame();
                        if (null == f) {
                            target.setPercent(c.getPercent());
                            target.setName(c.getName());
                        } else {
                            target.setPercent(c.getPercent() + f.getPercent());
                            target.setName(f.getName());
                        }
                        return target;
                    });

                }
            }
        }
        for (ProfileNode child : node.getChildren()) {
            final Map<String, Frame> tmp = build(child, flameNode, patterns);
            tmp.forEach((key, value) -> {
                result.merge(key, value, (first, second) -> {
                    first.setPercent(first.getPercent() + second.getPercent());
                    return first;
                });
            });
        }
        return result;
    }

    private static ProfileNode simple(ProfileNode node) {
        final List<ProfileNode> children = node.getChildren();
        List<ProfileNode> result = new ArrayList<>();
        boolean match = false;
        if (null != children) {
            for (ProfileNode child : children) {
                if (StringUtils.equals(child.getName(), node.getName())) {
                    match = true;
                    result.addAll(child.getChildren());
                } else {
                    result.add(simple(child));
                }
            }
        }
        node.setChildren(result);
        if (match) {
            return simple(node);
        }
        return node;
    }
}
