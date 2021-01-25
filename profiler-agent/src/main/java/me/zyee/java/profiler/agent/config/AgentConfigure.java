package me.zyee.java.profiler.agent.config;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/20
 */
public class AgentConfigure {
    private String libPath;
    private boolean dumpClassFile;

    public String getLibPath() {
        return libPath;
    }

    public void setLibPath(String libPath) {
        this.libPath = libPath;
    }

    public boolean isDumpClassFile() {
        return true;
    }

    public void setDumpClassFile(boolean dumpClassFile) {
        this.dumpClassFile = dumpClassFile;
    }
}
