package me.zyee.java.profiler.utils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/8/13
 */
public class OS {
    public enum OSType {
        //
        Undefined("undefined"),
        Linux("linux"),
        Windows("win"),
        Macintosh("mac"),
        Unknown("unknown");

        public final String simpleName;

        OSType(String simpleName) {
            this.simpleName = simpleName;
        }
    }

    private static OSType osType = OSType.Undefined;

    public static OSType getOSType() {
        if (osType == OSType.Undefined) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.startsWith("windows")) {
                osType = OSType.Windows;
            } else if (os.startsWith("linux")) {
                osType = OSType.Linux;
            } else if (os.startsWith("mac")) {
                osType = OSType.Macintosh;
            } else {
                osType = OSType.Unknown;
            }
        }
        return osType;
    }

}
