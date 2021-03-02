package me.zyee.java.profiler.agent.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.StringJoiner;
import me.zyee.java.profiler.bean.Memory;
import me.zyee.java.profiler.report.markdown.Table;
import me.zyee.java.profiler.report.markdown.Title;
import me.zyee.java.profiler.report.plugin.HtmlTableColumn;
import me.zyee.java.profiler.utils.FormatUtil;
import me.zyee.java.profiler.utils.OS;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class MemoryPlugin implements SystemHtmlPlugin {
    protected transient List<Memory> memories;

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

    @Override
    public String getTitle() {
        return "内存";
    }

    @Override
    public List<Object> getData() {
        final ImmutableList.Builder<Object> builder = ImmutableList.builder();
        for (int i = 0; i < memories.size(); i++) {
            Memory memory = memories.get(i);
            final ImmutableMap<Object, Object> build = ImmutableMap.builder().put("slot", memory.getBankLabel())
                    .put("type", memory.getMemoryType())
                    .put("cap", FormatUtil.formatBytes(memory.getCapacity()))
                    .put("band", memory.getManufacturer())
                    .put("clock", FormatUtil.formatHertz(memory.getClockSpeed()))
                    .put("broad", FormatUtil.formatValue(memory.getClockSpeed() * OS.getBitWidth() / 8, "B/s"))
                    .put("id", i)
                    .build();
            builder.add(build);
        }
        return builder.build();
    }

    @Override
    public List<HtmlTableColumn> getColumns() {
        return ImmutableList.<HtmlTableColumn>builder()
                .add(HtmlTableColumn.builder().setTitle("").setKey("slot").build(),
                        HtmlTableColumn.builder().setTitle("类型").setKey("type").build(),
                        HtmlTableColumn.builder().setTitle("容量").setKey("cap").build(),
                        HtmlTableColumn.builder().setTitle("制造商").setKey("band").build(),
                        HtmlTableColumn.builder().setTitle("时钟频率").setKey("clock").build(),
                        HtmlTableColumn.builder().setTitle("理论带宽").setKey("broad").build()).build();
    }

    @Override
    public boolean isExpandable() {
        return false;
    }
}
