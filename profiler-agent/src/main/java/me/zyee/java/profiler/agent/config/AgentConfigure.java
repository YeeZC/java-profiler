package me.zyee.java.profiler.agent.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import me.zyee.java.profiler.agent.converter.Converters;
import me.zyee.java.profiler.agent.converter.string.FromStringConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/20
 */
public class AgentConfigure {
    private String libPath;
    private boolean dumpClassFile;

    public String getLibPath() {
        return libPath;
    }

    public void setLibPath(String libPath) {
        this.libPath = libPath;
    }

    public boolean isDumpClassFile() {
        return dumpClassFile;
    }

    public void setDumpClassFile(boolean dumpClassFile) {
        this.dumpClassFile = dumpClassFile;
    }

    public static AgentConfigure fromArgs(String arg) {
        final AgentConfigure configure = new AgentConfigure();
        if (null == arg) {
            return configure;
        }
        final byte[] bytes = arg.replace(";", "\n").getBytes(StandardCharsets.UTF_8);
        Properties properties = new Properties();
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            properties.load(is);
        } catch (IOException e) {
            return configure;
        }
        final Field[] fields = FieldUtils.getAllFields(AgentConfigure.class);
        for (Field field : fields) {
            try {
                final String property = properties.getProperty(field.getName());
                if (StringUtils.isNotEmpty(property)) {
                    final FromStringConverter<?> converter = Converters.create(field.getType());
                    FieldUtils.writeField(field, configure, converter.convert(property), true);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return configure;
    }
}
