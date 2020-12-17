package me.zyee.java.profiler;

import java.util.List;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public interface NormalOperation extends Operation {
    List<Operation> getChildren();
}
