package me.zyee.java.profiler.report.plugin;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public enum Warning {
    theoretical("理论耗时未计算"),
    magnitude("超过理论耗时一个量级"),
    two("超过理论耗时2倍"),
    one_half("超过理论耗时1.5倍"),
    eighty_percent("未覆盖80%的耗时");

    String warning;

    Warning(String warning) {
        this.warning = warning;
    }
}
