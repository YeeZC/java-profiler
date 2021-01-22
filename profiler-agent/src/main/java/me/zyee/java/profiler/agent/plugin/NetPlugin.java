package me.zyee.java.profiler.agent.plugin;

import java.util.List;
import me.zyee.java.profiler.bean.Net;
import me.zyee.java.profiler.report.markdown.Table;
import me.zyee.java.profiler.report.markdown.Title;
import me.zyee.java.profiler.utils.FormatUtil;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class NetPlugin implements SystemPlugin {
    protected List<Net> nets;

    @Override
    public String render() {
        final Table.Builder builder = Table.builder().setTitle(Title.builder().setTitle("网络").setLevel(2).build())
                .setHeader("网卡", "带宽");
        nets.forEach(net -> builder.addRow(net.getName(), FormatUtil.formatValue(net.getSpeed(), "bps")));
        return builder.build().render();
    }


    @Override
    public NetPlugin apply(PluginInjector injector) {
        return injector.inject(this);
    }
}
