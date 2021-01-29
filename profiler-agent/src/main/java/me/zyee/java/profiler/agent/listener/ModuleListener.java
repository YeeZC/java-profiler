package me.zyee.java.profiler.agent.listener;

import java.lang.reflect.Field;
import java.util.List;
import javax.annotation.Resource;
import me.zyee.java.profiler.event.Before;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/29
 */
public class ModuleListener implements EventListener {
    private final EventWatcher watcher;

    public ModuleListener(EventWatcher watcher) {
        this.watcher = watcher;
    }

    @Override
    public boolean onEvent(Event event) throws Throwable {
        if (event instanceof Before) {
            Before before = (Before) event;
            final Object trigger = before.getTrigger();
            final Class<?> clazz = trigger.getClass();
            final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, Resource.class);
            for (Field field : fields) {
                if (field.getType().isAssignableFrom(EventWatcher.class)) {
                    final Object o = FieldUtils.readField(field, trigger, true);
                    if (o == null) {
                        FieldUtils.writeField(field, trigger, watcher, true);
                    }
                }
            }
        }
        return false;
    }
}
