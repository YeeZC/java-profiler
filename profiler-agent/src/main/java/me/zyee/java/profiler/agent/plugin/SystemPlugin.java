package me.zyee.java.profiler.agent.plugin;

import java.util.function.Function;
import me.zyee.java.profiler.report.plugin.Plugin;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public interface SystemPlugin extends Plugin, Function<PluginInjector, SystemPlugin> {
}
