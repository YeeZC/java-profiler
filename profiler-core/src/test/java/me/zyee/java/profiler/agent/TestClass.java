package me.zyee.java.profiler.agent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        final ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            service.submit(() -> System.out.println(Thread.currentThread().getName() + "-" + finalI));
            Thread.sleep(100);
        }
    }
}
