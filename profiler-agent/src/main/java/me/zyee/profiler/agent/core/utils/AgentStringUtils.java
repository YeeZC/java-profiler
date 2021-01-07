package me.zyee.profiler.agent.core.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/7
 */
public class AgentStringUtils {
    /**
     * java's classname to internal's classname
     *
     * @param javaClassName java's classname
     * @return internal's classname
     */
    public static String toInternalClassName(String javaClassName) {
        if (StringUtils.isEmpty(javaClassName)) {
            return javaClassName;
        }
        return javaClassName.replace('.', '/');
    }

    /**
     * internal's classname to java's classname
     * java/lang/String to java.lang.String
     *
     * @param internalClassName internal's classname
     * @return java's classname
     */
    public static String toJavaClassName(String internalClassName) {
        if (StringUtils.isEmpty(internalClassName)) {
            return internalClassName;
        }
        return internalClassName.replace('/', '.');
    }

}
