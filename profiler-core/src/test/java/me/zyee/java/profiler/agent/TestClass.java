package me.zyee.java.profiler.agent;

import me.zyee.java.profiler.annotation.Atoms;
import org.junit.Test;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class TestClass {
    @Atoms({})
    @Test
    public void test() throws InterruptedException {
        Thread.sleep(3000);
    }
}