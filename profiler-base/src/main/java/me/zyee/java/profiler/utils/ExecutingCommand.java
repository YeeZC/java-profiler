package me.zyee.java.profiler.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2020/2/17
 */
public class ExecutingCommand {
    private static final Logger log = LoggerFactory.getLogger(ExecutingCommand.class);

    public static List<String> runNative(String[] cmdToRunWithArgs) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmdToRunWithArgs);
        } catch (SecurityException | IOException e) {
            log.trace("Couldn't run command {}:", Arrays.toString(cmdToRunWithArgs), e);
            return new ArrayList<>(0);
        }

        ArrayList<String> sa = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sa.add(line);
            }
            p.waitFor();
        } catch (IOException e) {
            log.trace("Problem reading output from {}:", Arrays.toString(cmdToRunWithArgs), e);
            return new ArrayList<String>(0);
        } catch (InterruptedException e) {
            log.trace("Problem reading output from {}:", Arrays.toString(cmdToRunWithArgs), e);
            Thread.currentThread().interrupt();
        }
        return sa;
    }

    public static String getAnswerAt(String cmd2launch, int answerIdx) {
        List<String> sa = runNative(cmd2launch);

        if (answerIdx >= 0 && answerIdx < sa.size()) {
            return sa.get(answerIdx);
        }
        return "";
    }

    public static List<String> runNative(String cmdToRun) {
        String[] cmd = cmdToRun.split(" ");
        return runNative(cmd);
    }

    public static String getFirstAnswer(String cmd2launch) {
        return getAnswerAt(cmd2launch, 0);
    }
}
