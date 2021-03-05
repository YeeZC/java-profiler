package me.zyee.java.profiler.agent.listener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import javax.annotation.Resource;
import me.zyee.java.profiler.agent.plugin.PluginInjector;
import me.zyee.java.profiler.agent.plugin.SystemHtmlPlugin;
import me.zyee.java.profiler.agent.plugin.SystemPlugin;
import me.zyee.java.profiler.event.Before;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public class ReportListener implements EventListener {
    private final List<SystemHtmlPlugin> system = new ArrayList<>();

    @Override
    public boolean onEvent(Event event) throws Throwable {
        if (event instanceof Before) {
            Before before = (Before) event;
            final Object trigger = before.getTrigger();
            final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(trigger.getClass(), Resource.class);
            for (Field field : fields) {
                final Resource resource = field.getAnnotation(Resource.class);
                if ("system".equals(resource.name())) {
                    if (system.isEmpty()) {
                        final ServiceLoader<SystemPlugin> load = ServiceLoader.load(SystemPlugin.class,
                                ReportListener.class.getClassLoader());
                        for (SystemPlugin systemPlugin : load) {
                            system.add(systemPlugin.apply(PluginInjector.INSTANCE));
                        }
                    }
                    FieldUtils.writeField(field, trigger, Collections.unmodifiableList(system), true);
                }
            }
        }
        return false;
    }
}
