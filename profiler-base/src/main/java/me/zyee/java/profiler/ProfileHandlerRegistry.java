package me.zyee.java.profiler;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/17
 */
public class ProfileHandlerRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileHandlerRegistry.class);
    private static final ThreadLocal<ProfileHandler> LOCAL = new ThreadLocal<>();

    public static void register(ProfileHandler handler) {
        if (LOCAL.get() != null) {
            LOGGER.warn("ProfileHandler already registered");
            return;
        }
        LOCAL.set(handler);
    }

    public static void remove() {
        LOCAL.remove();
    }

    public static ProfileHandler getHandler() {
        final ProfileHandler handler = LOCAL.get();
        Objects.requireNonNull(handler, "ProfileHandler Not Register");
        return handler;
    }
}
