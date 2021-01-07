package me.zyee.profiler.agent.core.transformer;

import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.utils.Matcher;
import me.zyee.java.profiler.utils.SearchUtils;
import me.zyee.profiler.agent.core.enhancer.EventEnhancer;
import me.zyee.profiler.agent.core.utils.BehaviorStructure;
import me.zyee.profiler.agent.core.utils.ClassStructure;
import me.zyee.profiler.agent.utils.ObjectIds;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Set;
import java.util.stream.Collectors;

import static me.zyee.profiler.agent.core.utils.ClassStructureFactory.createClassStructure;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/6
 */
public class ProfilerTransformer implements ClassFileTransformer {
    private final Matcher<String> classMatcher;
    private final Matcher<String> methodMatcher;
    private final Event.Type[] listenEvents;
    private final int id;

    public ProfilerTransformer(String pattern, EventListener listener, Event.Type[] listenEvents) {
        this.listenEvents = listenEvents;
        this.id = ObjectIds.instance.identity(listener);
        final String[] split = pattern.split("#");
        classMatcher = SearchUtils.classNameMatcher(split[0]);
        if (split.length == 2) {
            methodMatcher = SearchUtils.classNameMatcher(split[1]);
        } else {
            methodMatcher = SearchUtils.classNameMatcher("*");
        }
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        final ClassStructure classStructure = getClassStructure(loader, classBeingRedefined, classfileBuffer);
        final String javaClassName = classStructure.getJavaClassName();
        if (!classMatcher.matching(javaClassName)) {
            return null;
        }
        Set<String> behaviorSignCodes = classStructure.getBehaviorStructures().stream()
                .filter(behavior -> methodMatcher.matching(behavior.getName()))
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
        if (bytes == classfileBuffer) {
            return null;
        }
        return bytes;
    }

    public int getId() {
        return id;
    }

    public Matcher<String> getClassMatcher() {
        return classMatcher;
    }

    private ClassStructure getClassStructure(final ClassLoader loader,
                                             final Class<?> classBeingRedefined,
                                             final byte[] srcByteCodeArray) {
        return null == classBeingRedefined
                ? createClassStructure(srcByteCodeArray, loader)
                : createClassStructure(classBeingRedefined);
    }

}
