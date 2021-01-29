package me.zyee.java.profiler.agent.operation;

import java.lang.reflect.Method;
import java.util.Optional;
import me.zyee.java.profiler.operation.impl.DefaultAtomOperation;
import me.zyee.java.profiler.utils.StringHelper;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/29
 */
public class OptionAtomMethodProcess extends BaseMethodProcess {
    public OptionAtomMethodProcess(Method method) {
        super(method);
    }

    @Override
    public Object process(Object delegate, Object... args) throws Throwable {
        final Object res = MethodUtils.invokeMethod(delegate, true, method.getName(), args);
        if (((Optional<?>) res).isPresent()) {
            final Object item = ((Optional<?>) res).get();
            final String str = StringHelper.toString(item);
            return Optional.of(StringHelper.fromArgs(str, DefaultAtomOperation.builder()).build());
        }
        return Optional.empty();
    }
}
