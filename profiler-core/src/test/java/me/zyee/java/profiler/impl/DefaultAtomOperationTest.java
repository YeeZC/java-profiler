package me.zyee.java.profiler.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public class DefaultAtomOperationTest {
    @Test
    public void test() {
        final DefaultAtomOperation hello = DefaultAtomOperation.builder().setName("Hello")
                .setCost(1024L)
                .setPattern("")
                .setExpect(10L)
                .setWhen(1000L).build();
        assertEquals("Hello", hello.getName());
        assertEquals(1024L, hello.getCost());
        assertEquals("", hello.getPattern());
        assertEquals(10L, hello.getExpect());
        assertEquals(1000L, hello.getWhen());
    }

}