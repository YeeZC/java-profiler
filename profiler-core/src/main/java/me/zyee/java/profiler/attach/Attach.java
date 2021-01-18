package me.zyee.java.profiler.attach;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import me.zyee.java.profiler.utils.FileUtils;
import me.zyee.java.profiler.utils.PidUtils;
import net.lingala.zip4j.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/7
 */
public class Attach {
    private static final Logger LOGGER = LoggerFactory.getLogger(Attach.class);
    private static final String VERSION_PROP_NAME = "java.specification.version";
    private static final String JAVA_VERSION_STR = System.getProperty(VERSION_PROP_NAME);
    private static final AtomicBoolean ATTACHED = new AtomicBoolean(false);

    private static void attach(Path agent, String javaPid) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        VirtualMachineDescriptor virtualMachineDescriptor = null;
        for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
            String pid = descriptor.id();
            if (pid.equals(javaPid)) {
                virtualMachineDescriptor = descriptor;
            }
        }
        VirtualMachine virtualMachine = null;
        try {
            if (null == virtualMachineDescriptor) {
                // 使用 attach(String pid) 这种方式
                virtualMachine = VirtualMachine.attach(javaPid);
            } else {
                virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
            }

            Properties targetSystemProperties = virtualMachine.getSystemProperties();
            String targetJavaVersion = javaVersionStr(targetSystemProperties);
            String currentJavaVersion = JAVA_VERSION_STR;
            if (targetJavaVersion != null && currentJavaVersion != null) {
                if (!targetJavaVersion.equals(currentJavaVersion)) {
                    LOGGER.warn("Current VM java version: {} do not match target VM java version: {}, attach may fail.",
                            currentJavaVersion, targetJavaVersion);
                    LOGGER.warn("Target VM JAVA_HOME is {}, arthas-boot JAVA_HOME is {}, try to set the same JAVA_HOME.",
                            targetSystemProperties.getProperty("java.home"), System.getProperty("java.home"));
                }
            }
            final String property = Paths.get(System.getProperty("java.io.tmpdir"), "me.zyee.java.profiler", "lib").toString();
            virtualMachine.loadAgent(agent.toString(), property);
        } finally {
            if (null != virtualMachine) {
                virtualMachine.detach();
            }
        }
    }

    private static String javaVersionStr(Properties props) {
        return (null != props) ? props.getProperty(VERSION_PROP_NAME) : null;
    }

    public static void attach() throws AgentInitializationException, AgentLoadException, AttachNotSupportedException, IOException {
        if (ATTACHED.compareAndSet(false, true)) {
            final Path path = Paths.get(System.getProperty("java.io.tmpdir"), "me.zyee.java.profiler");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            final Path agent = path.resolve("profiler-agent.jar");
            final Path zip = path.resolve("profiler-agent.zip");

            try (InputStream is = Attach.class.getResourceAsStream("/agent")) {
                final byte[] bytes = FileUtils.readAll(is);
                Files.write(zip, bytes);
                new ZipFile(zip.toFile()).extractAll(path.toString());
            }
            Attach.attach(agent, PidUtils.currentPid());
        }
    }
}
