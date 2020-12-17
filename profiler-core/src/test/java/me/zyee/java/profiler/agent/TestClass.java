package me.zyee.java.profiler.agent;

import org.junit.Test;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class TestClass {
    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + "-" + i);
            Thread.sleep(100);
        }
    }
}
