package me.zyee.java.profiler.agent.event.watcher;

import me.zyee.java.profiler.annotation.Profile;
import org.junit.Test;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/7
 */
public class TestCase {
    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            try {
                print(i);
                Thread.sleep(100);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @Profile
    public void print(int i) {
        int x = i + 100;
        System.out.println(plusRandom(x));
    }

    private double plusRandom(int i) {
        return i + Math.random() * 1000;
    }
}
