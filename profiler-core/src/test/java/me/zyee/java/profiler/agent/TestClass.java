package me.zyee.java.profiler.agent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.zyee.java.profiler.Operation;
import me.zyee.java.profiler.ProfileHandler;
import me.zyee.java.profiler.ProfileHandlerRegistry;
import me.zyee.java.profiler.annotation.Profile;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public class TestClass {
    @BeforeClass
    public static void register() {
        ProfileHandlerRegistry.register(new ProfileHandler() {
            @Override
            public Queue<Operation> next() {
                return new ConcurrentLinkedQueue<>();
            }
        });
    }


    @Test
    @Profile
    public void test() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            print(Thread.currentThread().getName() + "-" + i);
            Thread.sleep(100);
        }
    }

    public void print(String x) {
        System.out.println(x);
    }
}
