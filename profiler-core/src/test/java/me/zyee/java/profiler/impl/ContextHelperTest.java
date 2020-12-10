package me.zyee.java.profiler.impl;

import io.reactivex.rxjava3.core.Observable;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.Result;
import me.zyee.java.profiler.annotation.Atoms;
import one.profiler.Events;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/10
 */
@Atoms({})
public class ContextHelperTest {

    @Test
    public void test() {
        final Context context = ContextHelper.newContext("Hello", Events.CPU);
        if (null != context) {
            new BaseRunner(ContextHelperTest.class) {

                @Override
                public Result run() throws Exception {
                    Observable.interval(1, 1, TimeUnit.SECONDS)
                            .take(30)
                            .blockingSubscribe(System.out::println);
                    return new Result() {
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
            }.apply(context);
            final Queue<ProfileItem> queue = context.getProfileItems();
            System.out.println(queue.peek().getFlamePath());
        }
    }
}