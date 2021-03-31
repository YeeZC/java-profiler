package me.zyee.java.profiler.agent.plugin;

import me.zyee.java.profiler.report.plugin.HtmlPlugin;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public interface SystemPlugin extends HtmlPlugin {
    <T extends SystemPlugin> T apply(PluginInjector injector);
}
