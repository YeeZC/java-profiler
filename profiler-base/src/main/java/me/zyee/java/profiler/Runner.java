package me.zyee.java.profiler;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public interface Runner extends Task {
    Result run() throws Exception;

    default String name() {
        return "";
    }

    @Override
    default Result apply(Context context) {
        try {
            return run();
        } catch (Exception e) {
            return Result.failed(e);
        }
    }
}
