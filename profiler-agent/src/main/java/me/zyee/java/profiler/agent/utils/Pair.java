package me.zyee.java.profiler.agent.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2020/4/22
 */
public class Pair extends ArrayList<Object> {

    public Pair(Object... objects) {
        super(Arrays.asList(objects));
    }

}