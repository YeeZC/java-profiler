package me.zyee.java.profiler.agent.plugin;

import java.util.List;
import java.util.StringJoiner;
import me.zyee.java.profiler.bean.Memory;
import me.zyee.java.profiler.report.markdown.Table;
import me.zyee.java.profiler.report.markdown.Title;
import me.zyee.java.profiler.utils.FormatUtil;
import me.zyee.java.profiler.utils.OS;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class MemoryPlugin implements SystemPlugin {
    protected List<Memory> memories;

    @Override
    public String render() {
        final Table.Builder builder = Table.builder().setTitle(Title.builder().setTitle("内存").setLevel(2).build())
                .setHeader("", "类型", "容量", "制造商", "时钟频率", "理论带宽");
        memories.forEach(memory -> builder.addRow(memory.getBankLabel(),
                memory.getMemoryType(),
                FormatUtil.formatBytes(memory.getCapacity()),
                memory.getManufacturer(),
                FormatUtil.formatHertz(memory.getClockSpeed()),
                FormatUtil.formatValue(memory.getClockSpeed() * OS.getBitWidth() / 8, "B/s")
        ));

        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(builder.build().render()).add("` 理论带宽 = 时钟频率 x 系统位数 / 8 `").add("");
        return joiner.toString();
    }

    @Override
    public MemoryPlugin apply(PluginInjector injector) {
        return injector.inject(this);
    }
}
