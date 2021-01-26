package me.zyee.java.profiler.operation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/26
 */
public class AtomGroups {
    public static Map<AtomGroupType, AtomGroup> container = new ConcurrentHashMap<>();

    public static void register(AtomGroupType type, AtomGroup group) {
        container.put(type, group);
    }

    public static <T extends AtomGroup> T get(AtomGroupType type, Class<T> cast) {
        return cast.cast(container.get(type));
    }
}
