package me.zyee.java.profiler.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.JavaVersion;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2020/2/12
 */
public class ProcessUtils {
    private static String FOUND_JAVA_HOME = null;


    private static File getJpsPath(List<File> jpsList) {
        if (jpsList.size() > 1) {
            jpsList.sort((f1, f2) -> {
                try {
                    return f1.getCanonicalPath().length() - f2.getCanonicalPath().length();
                } catch (IOException e) {
                    // ignore
                }
                return -1;
            });
        }
        return jpsList.get(0);
    }

    public static String findJavaHome() {
        if (FOUND_JAVA_HOME != null) {
            return FOUND_JAVA_HOME;
        }

        String javaHome = System.getProperty("java.home");

        if (JavaVersion.JAVA_RECENT.atMost(JavaVersion.JAVA_1_8)) {
            File toolsJar = new File(javaHome, "sigar/lib/tools.jar");
            if (!toolsJar.exists()) {
                toolsJar = new File(javaHome, "../lib/tools.jar");
            }
            if (!toolsJar.exists()) {
                // maybe jre
                toolsJar = new File(javaHome, "../../lib/tools.jar");
            }

            if (toolsJar.exists()) {
                FOUND_JAVA_HOME = javaHome;
                return FOUND_JAVA_HOME;
            }

            if (!toolsJar.exists()) {
                String javaHomeEnv = System.getenv("JAVA_HOME");
                if (javaHomeEnv != null && !javaHomeEnv.isEmpty()) {
                    // $JAVA_HOME/lib/tools.jar
                    toolsJar = new File(javaHomeEnv, "sigar/lib/tools.jar");
                    if (!toolsJar.exists()) {
                        // maybe jre
                        toolsJar = new File(javaHomeEnv, "../lib/tools.jar");
                    }
                }

                if (toolsJar.exists()) {
                    FOUND_JAVA_HOME = javaHomeEnv;
                    return FOUND_JAVA_HOME;
                }

                throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome
                        + ", please try to start arthas-boot with full path java. Such as /opt/jdk/bin/java -jar arthas-boot.jar");
            }
        } else {
            FOUND_JAVA_HOME = javaHome;
        }
        return FOUND_JAVA_HOME;
    }

    public static File findJava() {
        String javaHome = findJavaHome();
        String[] paths = {"bin/java", "bin/java.exe", "../bin/java", "../bin/java.exe"};

        List<File> javaList = new ArrayList<File>();
        for (String path : paths) {
            File javaFile = new File(javaHome, path);
            if (javaFile.exists()) {
                javaList.add(javaFile);
            }
        }

        if (javaList.isEmpty()) {
            return null;
        }

        // find the shortest path, jre path longer than jdk path
        return getJpsPath(javaList);
    }

    public static File findToolsJar() {
        if (JavaVersion.JAVA_RECENT.atLeast(JavaVersion.JAVA_9)) {
            return null;
        }

        String javaHome = findJavaHome();
        File toolsJar = new File(javaHome, "sigar/lib/tools.jar");
        if (!toolsJar.exists()) {
            toolsJar = new File(javaHome, "../lib/tools.jar");
        }
        if (!toolsJar.exists()) {
            // maybe jre
            toolsJar = new File(javaHome, "../../lib/tools.jar");
        }

        if (!toolsJar.exists()) {
            throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome);
        }

        return toolsJar;
    }
}
