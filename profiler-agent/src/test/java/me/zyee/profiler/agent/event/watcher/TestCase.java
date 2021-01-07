package me.zyee.profiler.agent.event.watcher;

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

    public void print(int i) {
        System.out.println(i);
    }
}
