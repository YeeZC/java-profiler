package me.zyee.java.profiler.agent.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import me.zyee.java.profiler.bean.Cpu;
import me.zyee.java.profiler.report.markdown.Table;
import me.zyee.java.profiler.report.markdown.Title;
import me.zyee.java.profiler.report.plugin.HtmlTableColumn;
import me.zyee.java.profiler.utils.FormatUtil;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class CpuPlugin implements SystemHtmlPlugin {
    protected transient Cpu cpu;

    @Override
    public String render() {
        return Table.builder()
                .setTitle(Title.builder()
                        .setTitle("CPU")
                        .setLevel(2)
                        .build())
                .setHeader(Lists.newArrayList("品牌", "频率", "物理核", "逻辑核"))
                .addRow(Lists.newArrayList(cpu.getVendor(), FormatUtil.formatHertz(cpu.getFreq()),
                        String.valueOf(cpu.getPhysical()), String.valueOf(cpu.getLogical())
                )).build().render();
    }

    @Override
    public CpuPlugin apply(PluginInjector injector) {
        return injector.inject(this);
    }

    @Override
    public String getTitle() {
        return "CPU";
    }

    @Override
    public List<Object> getData() {
        return ImmutableList.builder().add(
                ImmutableMap.builder().put("band", cpu.getVendor())
                        .put("freq", FormatUtil.formatHertz(cpu.getFreq()))
                        .put("phy", String.valueOf(cpu.getPhysical()))
                        .put("logic", String.valueOf(cpu.getLogical()))
                        .put("id", 0)
                        .build()
        ).build();
    }

    @Override
    public List<HtmlTableColumn> getColumns() {
        return ImmutableList.<HtmlTableColumn>builder()
                .add(HtmlTableColumn.builder().setTitle("品牌").setKey("band").build(),
                        HtmlTableColumn.builder().setTitle("频率").setKey("freq").build(),
                        HtmlTableColumn.builder().setTitle("物理核").setKey("phy").build(),
                        HtmlTableColumn.builder().setTitle("逻辑核").setKey("logic").build()).build();
    }

    @Override
    public boolean isExpandable() {
        return false;
    }
}
