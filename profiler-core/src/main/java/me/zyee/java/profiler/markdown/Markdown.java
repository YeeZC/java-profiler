package me.zyee.java.profiler.markdown;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.flame.Frame;
import me.zyee.java.profiler.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/8/19
 */
public class Markdown {
    private static final String FORMAT = "| %s | %s | %s | %s | %.2f | %.2f | %.2f | %.2f | %.2f | %s |";
    private static final String ATOM_FORMAT = "| %s | %s | %.2f | %s |";
    private final double cost;
    private final Path outPath;
    private final ProfileNode root;
    private final Set<String> warnings = new HashSet<>();
    private final Set<String> errors = new HashSet<>();
    private final Map<String, Set<String>> errorInfos = new HashMap<>();
    private final Map<String, Set<String>> warnInfos = new HashMap<>();
    private Map<String, Long> theoreticalCost;
    private Map<String, Frame> frames;

    public Markdown(double cost, ProfileNode root, Path outPath) {
        this.cost = cost;
        this.outPath = outPath;
        this.root = root;
    }

    public void setTheoreticalCost(Map<String, Long> theoreticalCost) {
        this.theoreticalCost = theoreticalCost;
    }

    public void output() {
        try (InputStream is =
                     Markdown.class
                             .getResourceAsStream("/template.md")) {
            final byte[] buffer = FileUtils.readAll(is);
            final String s = new String(buffer);
            final String target = s.replace("$StepTable", toString())
                    .replace("$ProfileName", String.valueOf(root.getName()))
                    .replace("$AtomicTable", makeAtomTable(root).stream()
                            .map(data -> String.format(ATOM_FORMAT, data.toArray()))
                            .collect(Collectors.joining("\n")))
                    .replace("$Conclusion", makeConclusions());
            Files.write(outPath, target.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CharSequence makeGcStat(List<String> gcStat) {
        StringBuilder builder = new StringBuilder("|");
        final String s = gcStat.get(0);
        final int length = s.split("\\|").length;
        builder.append(s).append("|\n");
        for (int i = 0; i < length; i++) {
            builder.append("| --- ");
        }
        builder.append("|\n");
        for (int i = 1; i < gcStat.size(); i++) {
            builder.append("|").append(gcStat.get(1)).append("|\n");
        }
        return builder;
    }

    public String getName() {
        return root.getName();
    }

    public Map<String, Set<String>> getErrorInfos() {
        return errorInfos;
    }

    public Map<String, Set<String>> getWarnInfos() {
        return warnInfos;
    }

    public Map<String, Frame> getFrames() {
        return frames;
    }

    public void setFrames(Map<String, Frame> frames) {
        this.frames = frames;
    }

    @Override
    public String toString() {
        final List<List<Object>> lists = makeNodeData(root, "");

        final String collect = lists.stream().peek(data -> {
            String warning = (String) data.get(data.size() - 1);
            List<String> warnings = Lists.newArrayList(warning.split(","));
            final Double theoretical = (Double) data.get(4);
            final Double actual = (Double) data.get(5);
            final String pattern = StringUtils.isEmpty((String) data.get(3)) ?
                    (String) data.get(1) : (String) data.get(3);
            if (0D == theoretical) {
                warnings.add("理论耗时未计算");
                this.warnings.add(data.get(0) + "理论耗时未计算");
                if (!warnInfos.containsKey("理论耗时未计算")) {
                    warnInfos.put("理论耗时未计算", new HashSet<>());
                }
                warnInfos.get("理论耗时未计算").add(pattern);
            } else {
                if (actual / theoretical > 10) {
                    warnings.add("实际耗时大于理论耗时1个量级");
                    this.errors.add(data.get(0) + "实际耗时大于理论耗时1个量级");
                    if (!errorInfos.containsKey("实际耗时大于理论耗时1个量级")) {
                        errorInfos.put("实际耗时大于理论耗时1个量级", new HashSet<>());
                    }
                    errorInfos.get("实际耗时大于理论耗时1个量级").add(pattern);
                } else if (actual / theoretical > 2) {
                    warnings.add("实际耗时大于理论耗时2倍");
                    this.errors.add(data.get(0) + "实际耗时大于理论耗时2倍");
                    if (!errorInfos.containsKey("实际耗时大于理论耗时2倍")) {
                        errorInfos.put("实际耗时大于理论耗时2倍", new HashSet<>());
                    }
                    errorInfos.get("实际耗时大于理论耗时2倍").add(pattern);
                } else if (actual / theoretical > 1.5) {
                    this.warnings.add(data.get(0) + "实际耗时大于理论耗时1.5倍以上");
                    if (!errorInfos.containsKey("实际耗时大于理论耗时1.5倍以上")) {
                        errorInfos.put("实际耗时大于理论耗时1.5倍以上", new HashSet<>());
                    }
                    errorInfos.get("实际耗时大于理论耗时1.5倍以上").add(pattern);
                }
            }
            data.set(data.size() - 1, warnings.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(",")));
        }).map(row -> String.format(FORMAT, row.toArray())).collect(Collectors.joining("\n"));

        final double totalPercent = root.getChildren().stream().map(ProfileNode::getPattern)
                .mapToDouble(pattern -> {
                    final Frame frame = frames.get(pattern);
                    return Optional.ofNullable(frame).map(Frame::getPercent).orElse(0D);
                }).sum();

        if (totalPercent < 80) {
            errors.add("Profile未覆盖用例耗时的80%");
        }
        return collect;
    }

    private String makeConclusions() {
        StringBuilder builder = new StringBuilder("### Warnings\n\n");
        if (warnings.isEmpty()) {
            builder.append("* 没有警告\n\n");
        } else {
            int i = 1;
            for (String warning : warnings) {
                builder.append(i).append(". ").append(warning).append("\n");
                i++;
            }
        }
        builder.append("\n### Errors\n\n");
        if (errors.isEmpty()) {
            builder.append("* 没有错误\n\n");
        } else {
            int i = 1;
            for (String error : errors) {
                builder.append(i).append(". ").append(error).append("\n");
                i++;
            }
        }
        return builder.toString();
    }

    private List<List<Object>> makeAtomTable(ProfileNode node) {
        List<List<Object>> data = new ArrayList<>();
        if (null != node.getAtom()) {
            data.add(Lists.newArrayList(node.getName(), node.getPattern(), node.getAtom(), ""));
        } else {
            final List<List<Object>> lists = Optional.ofNullable(node.getChildren()).map(List::stream)
                    .map(stream -> stream.map(this::makeAtomTable).reduce((a, b) -> {
                        a.addAll(b);
                        return a;
                    }).orElse(Collections.emptyList())).orElse(Collections.emptyList());
            data.addAll(lists);
        }
        return data;
    }

    private List<List<Object>> makeNodeData(ProfileNode node, String rootName) {
        List<List<Object>> data = new ArrayList<>();
        String stepName = node == root ? "Profile" :
                StringUtils.isNoneEmpty(rootName) ?
                        rootName + "->" + node.getName() : node.getName();
        final List<ProfileNode> children = node.getChildren();
        if (null == children || children.isEmpty()) {
            double percent = 0D;
            String atomicName = "";
            String atomicPattern = "";
            double atomicCost = 0D;
            if (StringUtils.isNotEmpty(node.getPattern())) {
                final Frame frame = frames.get(node.getPattern());
                if (null != frame) {
                    percent = frame.getPercent();
                }
                final Long theoretical = theoreticalCost.get(node.getPattern());
                if (null != theoretical) {
                    atomicCost = theoretical;
                    atomicName = node.getName();
                    atomicPattern = node.getPattern();
                }
            }
            List<Object> row = Lists.newArrayList(stepName, node.getPattern(),
                    atomicName, atomicPattern, atomicCost, cost * percent / 100, 0D, percent, cost, "");
            data.add(row);
        } else {
            for (ProfileNode child : children) {
                data.addAll(makeNodeData(child, stepName));
            }
            double percent = 0D;
            if (StringUtils.isNotEmpty(node.getPattern())) {
                final Frame frame = frames.get(node.getPattern());
                if (null != frame) {
                    percent = frame.getPercent();
                }
            }

            if (!node.equals(root)) {
                final double stepCost = cost * percent / 100;
                List<Object> total = Lists.newArrayList(stepName, node.getPattern(), "", "", data.stream()
                        .peek(row -> row.set(6, 100 * ((Number) row.get(5)).doubleValue() / stepCost))
                        .mapToDouble(row -> ((Number) row.get(4)).doubleValue()).sum(), stepCost, 100D, percent, cost, "");
                data.add(total);
            }
        }

        return data;
    }
}
