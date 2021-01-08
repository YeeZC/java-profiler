package me.zyee.java.profiler.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2020/2/12
 */
public class ProcessUtils {
    private static final Logger log = LoggerFactory.getLogger(ProcessUtils.class);

    public static List<String> gcStat() {
        final File jstatFile = findJstat();
        String jstat = Optional.ofNullable(jstatFile).map(File::getAbsolutePath)
                .orElse("jstat");
        String[] cmd = new String[]{
                jstat, "-gcutil", PidUtils.currentPid()
        };
        final List<String> strings = ExecutingCommand.runNative(cmd);
        return strings.stream().map(String::trim)
                .map(str -> {
                    StringJoiner joiner = new StringJoiner("|");
                    final String[] split = str.split(" ");
                    for (String s : split) {
                        if (StringUtils.isNotEmpty(s)) {
                            joiner.add(s);
                        }
                    }
                    return joiner.toString();
                }).collect(Collectors.toList());
    }

    private static File findJstat() {
        // Try to find jstat under java.home and System env JAVA_HOME
        String javaHome = System.getProperty("java.home");
        String[] paths = {"bin/jstat", "bin/jstat.exe", "../bin/jstat", "../bin/jstat.exe"};

        List<File> jstatList = new ArrayList<File>();
        for (String path : paths) {
            File jstatFile = new File(javaHome, path);
            if (jstatFile.exists()) {
                log.debug("Found jstat: " + jstatFile.getAbsolutePath());
                jstatList.add(jstatFile);
            }
        }

        if (jstatList.isEmpty()) {
            log.debug("Can not find jstat under :" + javaHome);
            String javaHomeEnv = System.getenv("JAVA_HOME");
            log.debug("Try to find jstat under env JAVA_HOME :" + javaHomeEnv);
            for (String path : paths) {
                File jstatFile = new File(javaHomeEnv, path);
                if (jstatFile.exists()) {
                    log.debug("Found jstat: " + jstatFile.getAbsolutePath());
                    jstatList.add(jstatFile);
                }
            }
        }

        if (jstatList.isEmpty()) {
            log.debug("Can not find jstat under current java home: " + javaHome);
            return null;
        }

        // find the shortest path, jre path longer than jdk path
        return getPath(jstatList);
    }

    private static File getPath(List<File> list) {
        if (list.size() > 1) {
            list.sort((f1, f2) -> {
                try {
                    return f1.getCanonicalPath().length() - f2.getCanonicalPath().length();
                } catch (IOException e) {
                    // ignore
                }
                return -1;
            });
        }
        return list.get(0);
    }
}
