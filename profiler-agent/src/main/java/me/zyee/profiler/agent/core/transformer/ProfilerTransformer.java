package me.zyee.profiler.agent.core.transformer;

import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.filter.BehaviorFilter;
import me.zyee.profiler.agent.core.utils.AgentProxy;
import me.zyee.profiler.agent.core.utils.AgentStringUtils;
import me.zyee.profiler.agent.core.utils.Structure;
import me.zyee.profiler.agent.utils.ObjectIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/6
 */
public class ProfilerTransformer implements ClassFileTransformer {
    private final Logger logger = LoggerFactory.getLogger(ProfilerTransformer.class);
    private final Event.Type[] listenEvents;
    private final BehaviorFilter filter;
    private final int id;
    private final Set<String> transformed = new HashSet<>();

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
                            byte[] classfileBuffer) {
        final Structure structure = AgentProxy.newStructure(loader, classBeingRedefined, classfileBuffer);
        final String javaClassName = structure.getJavaClassName();
        if (!filter.classFilter(javaClassName)) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("matched class {}", javaClassName);
        }
        final Set<String> behaviorSignCodes = structure.getMatchesBehaviors(filter::methodFilter);
        if (behaviorSignCodes.isEmpty()) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("matched behaviors {}", behaviorSignCodes);
        }

        byte[] bytes = AgentProxy.newEnhancer().toByteCodeArray(loader,
                classfileBuffer, behaviorSignCodes, id, listenEvents);
        if (null == bytes) {
            return null;
        }
        if (Arrays.equals(bytes, classfileBuffer)) {
            return null;
        }
        transformed.add(AgentStringUtils.toJavaClassName(className));
        return bytes;
    }

    public int getId() {
        return id;
    }

    public BehaviorFilter getFilter() {
        return filter;
    }

    public Set<String> getTransformed() {
        return transformed;
    }
}
