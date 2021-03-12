package me.zyee.java.profiler.flame;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.RecursiveTask;
import java.util.function.Predicate;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/14
 */
class FlameNodeTask extends RecursiveTask<FlameNode> {
    private final double min;
    private final Elements elements;
    private final FlameNode root;
    private final Predicate<String> predicate;

    FlameNodeTask(double min, Elements elements, Predicate<String> predicate) {
        this(min, elements, null, predicate);
    }

    FlameNodeTask(double min, Elements elements, FlameNode root, Predicate<String> predicate) {
        this.min = min;
        this.elements = elements;
        this.predicate = predicate;
        if (null != root) {
            this.root = root;
        } else {
            this.root = new FlameNode();
        }
    }

    @Override
    protected FlameNode compute() {
        for (Element element : elements) {
            Optional.ofNullable(element.selectFirst("span")).ifPresent(el -> {
                final String span = el.text().replace("/", ".");
                if (predicate.test(span)) {
                    final String div = element.selectFirst("div").text();
                    final double percent = Double.parseDouble(StringUtils.substringBetween(div, "] ", "%"));
                    if (percent < min) {
                        return;
                    }

                    final FlameNode node = new FlameNode();
                    node.setName(span);
                    final String count = StringUtils.substringBetween(div, "% ", " self");
                    final String selfPercent = StringUtils.substringBetween(div, ": ", "%");
                    final String selfCount = StringUtils.substringAfterLast(div, "% ");
                    node.setPercent(percent);
                    node.setCount(Long.parseLong(count.replace(",", "")));
                    node.setSelfPercent(Double.parseDouble(selfPercent));
                    node.setSelfCount(Long.parseLong(selfCount.replace(",", "")));
                    root.add(makeChildFlameNode(node, element));
                } else {
                    makeChildFlameNode(root, element);
                }

            });
        }
        return new SimpleTask(root).fork().join();
    }

    private FlameNode makeChildFlameNode(FlameNode node, Element element) {
        final Element ul = element.selectFirst("ul");
        if (null != ul) {
            final FlameNodeTask task = new FlameNodeTask(min, ul.children(), node, predicate);
            return task.fork().join();
        }
        return node;
    }

    private static class SimpleTask extends RecursiveTask<FlameNode> {
        private final FlameNode node;

        SimpleTask(FlameNode node) {
            this.node = node;
        }

        @Override
        protected FlameNode compute() {
            final List<FlameNode> children = node.getChildren();
            List<FlameNode> result = new ArrayList<>();
            boolean match = false;
            for (FlameNode child : children) {
                if (StringUtils.equals(child.getName(), node.getName())) {
                    match = true;
                    result.addAll(child.getChildren());
                } else {
                    result.add(new SimpleTask(child).fork().join());
                }
            }
            node.setChildren(result);
            if (match) {
                return new SimpleTask(node).fork().join();
            }
            return node;
        }
    }
}
