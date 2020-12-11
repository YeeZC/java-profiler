package me.zyee.java.profiler;

import com.google.common.collect.Lists;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import me.zyee.java.profiler.impl.ContextHelper;
import one.profiler.Events;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public abstract class BaseProfilerCore implements ProfilerCore {
    private final List<Task> before = new ArrayList<>();
    private final List<Task> after = new ArrayList<>();
    private final List<Task> failed = new ArrayList<>();
    private final List<Task> finished = new ArrayList<>();

    @Override
    public void profile(Runner runner) {

        Observable.create((ObservableOnSubscribe<Context>) emitter -> {
            Context context = ContextHelper.newContext(runner.name(), Events.CPU);
            if (null != context) {
                emitter.onNext(context);
            } else {
                emitter.onError(new UnsupportedOperationException());
            }

            context = ContextHelper.newContext(runner.name(), Events.ALLOC);
            if (null != context) {
                emitter.onNext(context);
            } else {
                emitter.onError(new UnsupportedOperationException());
            }
        }).blockingSubscribe(new SubscribeConsumer(runner), Throwable::printStackTrace);

    }

    private static class TaskObservable implements ObservableOnSubscribe<Context> {
        private final List<Task> tasks;
        private final Context context;

        public TaskObservable(List<Task> tasks, Context context) {
            this.tasks = tasks;
            this.context = context;
        }

        @Override
        public void subscribe(@NonNull ObservableEmitter<Context> emitter) {
            for (Task task : tasks) {
                final Result apply = task.apply(context);
                if (apply.isOk()) {
                    emitter.onNext(context);
                } else {
                    emitter.onError(apply.getThrowable());
                }
            }
        }
    }

    private class SubscribeConsumer implements Consumer<Context> {
        private final Task runner;

        public SubscribeConsumer(Task runner) {
            this.runner = runner;
        }

        @Override
        public void accept(Context context) throws Throwable {
            Observable.merge(
                    Observable.concat(Observable.create(new TaskObservable(before, context)),
                            Observable.create(new TaskObservable(Lists.newArrayList(runner), context))),
                    Observable.create(new TaskObservable(after, context)))
                    .subscribe(ctx -> {
                            },
                            error -> Observable.fromIterable(failed).subscribe(task -> task.apply(context)),
                            () -> Observable.fromIterable(finished).subscribe(task -> task.apply(context)));
        }
    }
}
