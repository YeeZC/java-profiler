package me.zyee.java.profiler.flame;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.utils.GroupMatcher;
import me.zyee.java.profiler.utils.OS;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/8/6
 */
public class FlameParser {

    public static Map<String, Frame> parse(Path path, ProfileNode node, Map<String, GroupMatcher.Or<String>> patterns) throws IOException {
        if (OS.getOSType() == OS.OSType.Windows) {
            throw new UnsupportedOperationException();
        }
        Map<String, Frame> result = new HashMap<>();
        final Document parse = Jsoup.parse(path.toFile(), "UTF-8");
        final Elements select = parse.select("ul.tree>li");
        final Frame root = new Frame();
        final FlameNode rootNode = new FlameNode();
        buildFlameTree(select, rootNode, patterns);
        root.setName("Profile");

//        buildFrame(rootNode, root, patterns, result);
        FlameNode simple = simple(rootNode);
        ProfileNode simple1 = simple(node);
        buildFrame(simple1, simple, patterns, result);
        return result;
    }

    private static void buildFrame(ProfileNode node, FlameNode flameNode, Map<String, GroupMatcher.Or<String>> patterns, Map<String, Frame> result) {
        final String pattern = node.getPattern();
        final List<FlameNode> children = flameNode.getChildren();
        if (StringUtils.isNotEmpty(pattern)) {
            final GroupMatcher.Or<String> matcher = patterns.get(pattern);
            final List<FlameNode> collect = children.stream().filter(n -> matcher.matching(n.getName()))
                    .collect(Collectors.toList());
            if (!collect.isEmpty()) {
                for (FlameNode c : collect) {
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
                    for (ProfileNode child : node.getChildren()) {
                        buildFrame(child, c, patterns, result);
                    }
                }
            } else {
                children.forEach(n -> buildFrame(node, n, patterns, result));
            }

        } else {
            for (ProfileNode child : Optional.ofNullable(node.getChildren()).orElse(Collections.emptyList())) {
                buildFrame(child, flameNode, patterns, result);
            }
        }

    }

    private static void buildFlameTree(Elements select, FlameNode root, Map<String, GroupMatcher.Or<String>> patterns) {
        for (Element element : select) {
            Optional.ofNullable(element.selectFirst("span")).ifPresent(el -> {
                final String span = el.text().replace("/", ".");
                final boolean match = patterns.values().stream().anyMatch(matcher -> matcher.matching(span));
                if (match) {
                    final FlameNode node = new FlameNode();
                    node.setName(span);
                    final String div = element.selectFirst("div").text();
                    final String percent = StringUtils.substringBetween(div, "] ", "%");
                    final String count = StringUtils.substringBetween(div, "% ", " self");
                    final String selfPercent = StringUtils.substringBetween(div, ": ", "%");
                    final String selfCount = StringUtils.substringAfterLast(div, "% ");
                    node.setPercent(Double.parseDouble(percent));
                    node.setCount(Long.parseLong(count.replace(",", "")));
                    node.setSelfPercent(Double.parseDouble(selfPercent));
                    node.setSelfCount(Long.parseLong(selfCount.replace(",", "")));
                    root.add(node);
                    final Element ul = element.selectFirst("ul");
                    if (null != ul) {
                        buildFlameTree(ul.children(), node, patterns);
                    }
                } else {
                    final Element ul = element.selectFirst("ul");
                    if (null != ul) {
                        buildFlameTree(ul.children(), root, patterns);
                    }
                }
            });

        }
    }

    private static void buildFrame(FlameNode select, Frame root, Map<String, GroupMatcher.Or<String>> patterns, Map<String, Frame> result) {
        for (final FlameNode element : select.getChildren()) {
            final Frame node = new Frame();
            node.setParent(root);
            node.setName(element.getName());
            for (Map.Entry<String, GroupMatcher.Or<String>> entry : patterns.entrySet()) {
                if (entry.getValue().matching(element.getName())) {
                    result.compute(entry.getKey(), (s, kvProfile) -> {
                        if (null == kvProfile) {
                            node.setPercent(element.getPercent());
                            node.setCount(element.getCount());
                            node.setSelfPercent(element.getSelfPercent());
                            node.setSelfCount(element.getSelfCount());
                            return node;
                        }
                        Frame n = root;
                        while (n != kvProfile && n.getParent() != null) {
                            n = n.getParent();
                        }
                        if (n != kvProfile) {
                            node.setPercent(element.getPercent() + kvProfile.getPercent());
                            node.setCount(element.getCount() + kvProfile.getCount());
                            node.setSelfPercent(element.getSelfPercent() + kvProfile.getSelfPercent());
                            node.setSelfCount(element.getSelfCount() + kvProfile.getSelfCount());
                            return node;
                        }
                        return kvProfile;

                    });
                }
            }
            buildFrame(element, node, patterns, result);
        }
    }

    private static FlameNode simple(FlameNode node) {
        final List<FlameNode> children = node.getChildren();
        List<FlameNode> result = new ArrayList<>();
        boolean match = false;
        for (FlameNode child : children) {
            if (StringUtils.equals(child.getName(), node.getName())) {
                match = true;
                result.addAll(child.getChildren());
            } else {
                result.add(simple(child));
            }
        }
        result.forEach(item -> item.setParent(node));
        node.setChildren(result);
        if (match) {
            return simple(node);
        }
        return node;
    }

    private static ProfileNode simple(ProfileNode node) {
        final List<ProfileNode> children = node.getChildren();
        List<ProfileNode> result = new ArrayList<>();
        boolean match = false;
        for (ProfileNode child : children) {
            if (StringUtils.equals(child.getName(), node.getName())) {
                match = true;
                result.addAll(child.getChildren());
            } else {
                result.add(simple(child));
            }
        }
        result.forEach(item -> item.setParent(node));
        node.setChildren(result);
        if (match) {
            return simple(node);
        }
        return node;
    }
}