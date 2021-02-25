package me.zyee.java.profiler.report.plugin;

import java.util.List;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/2/24
 */
public interface HtmlPlugin {

    String getTitle();
    List<Object> getData();
    List<HtmlTableColumn> getColumns();

    boolean isExpandable();

    default PluginType getType() {
        return PluginType.table;
    }
}
