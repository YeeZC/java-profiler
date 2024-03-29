package me.zyee.java.profiler.utils;

import java.util.function.Supplier;

/**
 * 懒加载
 *
 * @param <T> 懒加载类型
 * @author yee
 */
public abstract class LazyGet<T> {

    private volatile boolean isInit = false;
    private volatile T object;

    abstract protected T initialValue() throws Throwable;

    public T get() {

        if (isInit) {
            return object;
        }

        // lazy get
        try {
            object = initialValue();
            isInit = true;
            return object;
        } catch (Throwable throwable) {
            throw new LazyGetUnCaughtException(throwable);
        }

    }

    private static class LazyGetUnCaughtException extends RuntimeException {
        LazyGetUnCaughtException(Throwable cause) {
            super(cause);
        }
    }

    public static class SupplierLazyGet<T> extends LazyGet<T> {
        private final Supplier<T> supplier;

        public SupplierLazyGet(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        protected T initialValue() throws Throwable {
            return supplier.get();
        }
    }
}
