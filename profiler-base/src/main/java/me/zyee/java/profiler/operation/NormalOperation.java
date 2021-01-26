package me.zyee.java.profiler.operation;

import java.util.List;
import me.zyee.java.profiler.Operation;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public interface NormalOperation extends Operation {
    List<Operation> getChildren();
}
