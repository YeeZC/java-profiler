package me.zyee.java.profiler.report.plugin;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.StringJoiner;
import me.zyee.java.profiler.report.markdown.Table;
import me.zyee.java.profiler.report.markdown.Title;
import me.zyee.java.profiler.utils.FormatUtil;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class JvmPlugin implements Plugin {
    @Override
    public String render() {
        final Table.Builder gc = Table.builder().setTitle(Title.builder().setTitle("Garbage Collector").setLevel(2).build())
                .setHeader("GC类型", "次数", "耗时");
        final List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        gcBeans.forEach(bean -> gc.addRow(bean.getName(),
                FormatUtil.formatValue(bean.getCollectionCount(), "次"),
                FormatUtil.formatMilliseconds(bean.getCollectionTime())));
        final Table.Builder memory = Table.builder().setTitle(Title.builder().setTitle("Memory").setLevel(2).build())
                .setHeader("", "初始化", "最大", "已使用");
        final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        final MemoryUsage heap = memoryBean.getHeapMemoryUsage();
        final MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();
        memory.addRow("Heap", FormatUtil.formatBytes(heap.getInit()),
                FormatUtil.formatBytes(heap.getMax()),
                FormatUtil.formatBytes(heap.getUsed()))
                .addRow("Non Heap", FormatUtil.formatBytes(nonHeap.getInit()),
                        FormatUtil.formatBytes(nonHeap.getMax()),
                        FormatUtil.formatBytes(nonHeap.getUsed()));
        StringJoiner joiner = new StringJoiner("\n");
        return joiner.add(gc.build().render()).add(memory.build().render()).toString();
    }
}
