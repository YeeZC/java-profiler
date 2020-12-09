package me.zyee.java.profiler;

import java.nio.file.Path;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/9
 */
public class ProfileItem {
    private final String profileName;
    private Path flamePath;
    private long cost;
    private Throwable throwable;

    public ProfileItem(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileName() {
        return profileName;
    }

    public Path getFlamePath() {
        return flamePath;
    }

    public void setFlamePath(Path flamePath) {
        this.flamePath = flamePath;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
