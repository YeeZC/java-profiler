package me.zyee.java.profiler.agent.plugin;

import com.google.common.collect.Lists;
import me.zyee.java.profiler.bean.Cpu;
import me.zyee.java.profiler.report.markdown.Table;
import me.zyee.java.profiler.report.markdown.Title;
import me.zyee.java.profiler.utils.FormatUtil;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class CpuPlugin implements SystemPlugin {
    protected Cpu cpu;

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
}
