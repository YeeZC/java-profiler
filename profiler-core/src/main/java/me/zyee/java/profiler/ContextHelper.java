package me.zyee.java.profiler;

import me.zyee.java.profiler.impl.ContextImpl;

import java.util.concurrent.atomic.AtomicReference;

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

    public static Context newContext() {
        Context context = new ContextImpl();
        setContext(context);
        return context;
    }
}
