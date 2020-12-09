package me.zyee.java.profiler.impl;

import me.zyee.java.profiler.Result;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/9
 */
public class ProfileJUnitRunner extends BaseRunner {
    public ProfileJUnitRunner(Class<?> targetClass) {
        super(targetClass);
    }

    @Override
    public Result run() {
        final org.junit.runner.Result result = JUnitCore.runClasses(targetClass);
        return new Result() {
            @Override
            public boolean isOk() {
                return result.wasSuccessful();
            }

            @Override
            public Throwable getThrowable() {
                if (!result.wasSuccessful()) {
                    final Failure failure = result.getFailures().get(0);
                    return failure.getException();
                }
                return null;
            }
        };
    }
}
