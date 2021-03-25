package me.zyee.java.profiler;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/2
 */
public interface Result {
    boolean isOk();

    Throwable getThrowable();

    static Result failed(Throwable t) {
        return new Result() {
            @Override
            public boolean isOk() {
                return false;
            }

            @Override
            public Throwable getThrowable() {
                return t;
            }
        };
    }

    Result SUCCESS = new Result() {
        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public Throwable getThrowable() {
            return null;
        }
    };
}
