package me.zyee.java.profiler.event;

import java.nio.file.Path;
import java.util.Optional;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileHandler;
import me.zyee.java.profiler.ProfileHandlerRegistry;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.Profiler;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.impl.ContextHelper;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/8
 */
public class MethodProfileListener implements EventListener {
    private long start;
    private ProfileItem item;
    private Context context;
    private Profiler profiler;

    @Override
    public boolean onEvent(Event event) throws Throwable {
        switch (event.type()) {
            case BEFORE:
                return onBefore((Before) event);
            case RETURN:
                return onReturn();
            case THROWS:
                return onThrows((Throws) event);
            default:
        }
        return false;
    }

    private boolean onBefore(Before before) {
        context = ContextHelper.getContext().resolve(before.getTriggerMethod()
                + System.currentTimeMillis());
        item = new ProfileItem(before.getTriggerMethod());
        profiler = context.getProfiler();
        if (null != profiler) {
            profiler.start();
            start = System.currentTimeMillis();
        }
        return false;
    }

    private boolean onReturn() {
        if (null != profiler) {
            try {
                final ProfileHandler handler = ProfileHandlerRegistry.getHandler();
                item.offer(handler.next());
                item.setCost(System.currentTimeMillis() - start);
                final Path stop = profiler.stop();
                item.setFlamePath(stop);
                Optional.ofNullable(context.getProfileItems()).ifPresent(queue -> queue.offer(item));
                System.out.println("profile tree 输出路径 " + stop);
            } catch (NullPointerException e) {
                item.setThrowable(e);
            }
        }
        return false;
    }

    private boolean onThrows(Throws throwsEvent) {
        item.setThrowable(throwsEvent.getThrowable());
        return onReturn();
    }

}
