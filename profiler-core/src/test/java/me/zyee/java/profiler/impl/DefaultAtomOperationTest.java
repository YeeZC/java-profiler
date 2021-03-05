package me.zyee.java.profiler.impl;

import java.util.Random;
import org.junit.Test;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public class DefaultAtomOperationTest {
    @Test
    public void test() {
//        final DefaultAtomOperation hello = DefaultAtomOperation.builder().setName("Hello")
//                .setCost(1024L)
//                .setPattern("")
//                .setExpect(10L)
//                .setWhen(1000L).build();
//        assertEquals("Hello", hello.getName());
//        assertEquals(1024L, hello.getCost());
//        assertEquals("", hello.getPattern());
//        assertEquals(10L, hello.getExpect());
//        assertEquals(1000L, hello.getWhen());
        final Random random = new Random();
        System.out.println(random.nextBoolean());
        System.out.println(random.nextInt());
        System.out.println(random.nextInt());
        System.out.println(random.nextLong());
        System.out.println(random.nextGaussian());

        int[][] table = new int[10][];
    }

}