package me.zyee.java.profiler.impl;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.utils.OS;
import one.profiler.Events;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/9
 */
public class ContextHelper {
    private static final AtomicReference<Context> CONTEXT_ATOMIC_REFERENCE = new AtomicReference<>();

    public static Context getContext() {
        Context context;
        do {
            context = CONTEXT_ATOMIC_REFERENCE.get();
        } while (context == null);
        return context;
    }

    public static void setContext(Context context) {
        boolean success = false;
        do {
            success = CONTEXT_ATOMIC_REFERENCE.compareAndSet(null, context);
        } while (!success);
    }

    public static void removeContext(Context context) {
        CONTEXT_ATOMIC_REFERENCE.compareAndSet(context, null);
    }

    public static Context newContext(String name, Events event, Set<String> excludes) {
        switch (OS.getOSType()) {
            case Linux:
            case Macintosh: {
                final PosixContext context = new PosixContext(name, event, excludes);
                setContext(context);
                return context;
            }
            case Windows: {
                final WindowsContext context = new WindowsContext(name);
                setContext(context);
                return context;
            }
            default:
                return null;
        }
    }
}
