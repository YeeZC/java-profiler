package me.zyee.java.profiler.bean;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/21
 */
public enum IOSpeedType {
    // memory
    HEAP_R,
    HEAP_W,
    NON_HEAP_R,
    NON_HEAP_W,

    // file
    BIO_R,
    BIO_W,
    NIO_R,
    NIO_W
}
