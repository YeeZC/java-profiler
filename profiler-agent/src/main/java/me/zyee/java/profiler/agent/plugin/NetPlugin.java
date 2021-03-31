package me.zyee.java.profiler.agent.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.zyee.java.profiler.bean.Net;
import me.zyee.java.profiler.report.plugin.HtmlTableColumn;
import me.zyee.java.profiler.utils.FormatUtil;

import java.util.List;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class NetPlugin implements SystemPlugin {
    protected transient List<Net> nets;

    @Override
    public NetPlugin apply(PluginInjector injector) {
        return injector.inject(this);
    }

    @Override
    public String getTitle() {
        return "网络";
    }

    @Override
    public List<Object> getData() {
        final ImmutableList.Builder<Object> builder = ImmutableList.builder();
        for (int i = 0; i < nets.size(); i++) {
            Net memory = nets.get(i);
            final ImmutableMap<Object, Object> build = ImmutableMap.builder()
                    .put("net", memory.getName())
                    .put("broard", FormatUtil.formatValue(memory.getSpeed(), "bps"))
                    .put("id", i)
                    .build();
            builder.add(build);
        }
        return builder.build();
    }

    @Override
    public List<HtmlTableColumn> getColumns() {
        return ImmutableList.<HtmlTableColumn>builder()
                .add(HtmlTableColumn.builder().setTitle("网卡").setKey("net").build(),
                        HtmlTableColumn.builder().setTitle("带宽").setKey("broard").build()).build();
    }

    @Override
    public boolean isExpandable() {
        return false;
    }
}
