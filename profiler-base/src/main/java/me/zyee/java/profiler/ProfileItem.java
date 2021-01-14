package me.zyee.java.profiler;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

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
    private final Queue<Operation> nodes = new ConcurrentLinkedQueue<>();
    private final Map<String, Supplier<Long>> actualCost = new HashMap<>();

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

    public Queue<Operation> getNodes() {
        return nodes;
    }

    public void offer(Queue<Operation> nodes) {
        this.nodes.addAll(nodes);
    }

    public Map<String, Supplier<Long>> getActualCost() {
        return actualCost;
    }

    public void setActualCost(Map<String, Supplier<Long>> costs) {
        this.actualCost.putAll(costs);
    }
}
