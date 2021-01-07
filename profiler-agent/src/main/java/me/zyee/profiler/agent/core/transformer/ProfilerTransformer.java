package me.zyee.profiler.agent.core.transformer;

import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.filter.BehaviorFilter;
import me.zyee.profiler.agent.core.enhancer.EventEnhancer;
import me.zyee.profiler.agent.core.utils.BehaviorStructure;
import me.zyee.profiler.agent.core.utils.ClassStructure;
import me.zyee.profiler.agent.utils.ObjectIds;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static me.zyee.profiler.agent.core.utils.ClassStructureFactory.createClassStructure;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/6
 */
public class ProfilerTransformer implements ClassFileTransformer {
    private final Event.Type[] listenEvents;
    private final BehaviorFilter filter;
    private final int id;

    public ProfilerTransformer(BehaviorFilter filter, EventListener listener, Event.Type[] listenEvents) {
        this.listenEvents = listenEvents;
        this.id = ObjectIds.instance.identity(listener);
        this.filter = filter;
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        final ClassStructure classStructure = getClassStructure(loader, classBeingRedefined, classfileBuffer);
        final String javaClassName = classStructure.getJavaClassName();
        if (!filter.classFilter(javaClassName)) {
            return null;
        }
        Set<String> behaviorSignCodes = classStructure.getBehaviorStructures().stream()
                .filter(behavior -> filter.methodFilter(behavior.getName(), behavior.getAnnotationTypeClassStructures().stream()
                        .map(ClassStructure::getJavaClassName)))
                .map(BehaviorStructure::getSignCode)
                .collect(Collectors.toSet());
        if (behaviorSignCodes.isEmpty()) {
            return null;
        }
        final byte[] bytes = new EventEnhancer().toByteCodeArray(loader,
                classfileBuffer,
                behaviorSignCodes,
                id,
                listenEvents);
        if (Arrays.equals(bytes, classfileBuffer)) {
            return null;
        }
        return bytes;
    }

    public int getId() {
        return id;
    }

    public BehaviorFilter getFilter() {
        return filter;
    }

    private ClassStructure getClassStructure(final ClassLoader loader,
                                             final Class<?> classBeingRedefined,
                                             final byte[] srcByteCodeArray) {
        return null == classBeingRedefined
                ? createClassStructure(srcByteCodeArray, loader)
                : createClassStructure(classBeingRedefined);
    }

}
