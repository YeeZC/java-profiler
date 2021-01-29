package me.zyee.java.profiler.agent.operation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.zyee.java.profiler.operation.AtomOperation;
import me.zyee.java.profiler.operation.impl.DefaultAtomOperation;
import me.zyee.java.profiler.utils.StringHelper;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/29
 */
public class AtomListMethodProcess extends BaseMethodProcess {

    public AtomListMethodProcess(Method method) {
        super(method);
    }

    @Override
    public Object process(Object delegate, Object... args) throws Throwable {
        final Object res = MethodUtils.invokeMethod(delegate, true, method.getName(), args);
        List<AtomOperation> result = new ArrayList<>();
        ((List<?>) res).forEach(item -> {
            final String str = StringHelper.toString(item);
            result.add(StringHelper.fromArgs(str, DefaultAtomOperation.builder()).build());
        });
        return result;
    }
}
