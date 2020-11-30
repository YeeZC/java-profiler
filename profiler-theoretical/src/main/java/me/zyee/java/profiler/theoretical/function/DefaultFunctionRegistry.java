package me.zyee.java.profiler.theoretical.function;


import com.google.common.collect.ImmutableList;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import com.googlecode.aviator.runtime.type.AviatorString;

import java.util.Arrays;
import java.util.Map;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public class DefaultFunctionRegistry implements FunctionRegistry {
    @Override
    public FunctionRegistry register(Function<?> function) {
        final BaseFunction baseFunction = new BaseFunction() {

            @Override
            public String getName() {
                return function.getName();
            }

            @Override
            protected AviatorObject invoke(Map<String, Object> env, AviatorObject... args) {
                if (args.length != function.getParameterCount()) {
                    return throwArity(args.length);
                }
                final Object eval = function.eval(Arrays.stream(args).map(arg -> arg.getValue(env)).toArray());
                if (eval instanceof String) {
                    return new AviatorString((String) eval);
                } else if (eval instanceof Number) {
                    return AviatorNumber.valueOf(eval);
                } else {
                    return AviatorRuntimeJavaType.valueOf(eval);
                }
            }
        };
        AviatorEvaluator.addFunction(baseFunction);
        return this;
    }

    abstract static class BaseFunction extends AbstractFunction {

        /**
         * call
         *
         * @param env
         * @param args
         * @return
         */
        abstract protected AviatorObject invoke(Map<String, Object> env, AviatorObject... args);

        @Override
        public AviatorObject call(Map<String, Object> env) {
            return invoke(env);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            return invoke(env, arg1);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            return invoke(env, arg1, arg2);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
            return invoke(env, arg1, arg2, arg3);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4) {
            return invoke(env, arg1, arg2, arg3, arg4);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7, AviatorObject arg8) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                                  AviatorObject arg12) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                                  AviatorObject arg12, AviatorObject arg13) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                                  AviatorObject arg12, AviatorObject arg13, AviatorObject arg14) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                                  AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);

        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                                  AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15,
                                  AviatorObject arg16) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);

        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                                  AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15,
                                  AviatorObject arg16, AviatorObject arg17) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14,
                    arg15, arg16, arg17);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                                  AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15,
                                  AviatorObject arg16, AviatorObject arg17, AviatorObject arg18) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14,
                    arg15, arg16, arg17, arg18);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                                  AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15,
                                  AviatorObject arg16, AviatorObject arg17, AviatorObject arg18, AviatorObject arg19) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14,
                    arg15, arg16, arg17, arg18, arg19);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                                  AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15,
                                  AviatorObject arg16, AviatorObject arg17, AviatorObject arg18, AviatorObject arg19,
                                  AviatorObject arg20) {
            return invoke(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14,
                    arg15, arg16, arg17, arg18, arg19, arg20);
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                                  AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                                  AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                                  AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15,
                                  AviatorObject arg16, AviatorObject arg17, AviatorObject arg18, AviatorObject arg19,
                                  AviatorObject arg20, AviatorObject... args) {
            final ImmutableList<AviatorObject> build = ImmutableList.<AviatorObject>builder().add(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14,
                    arg15, arg16, arg17, arg18, arg19, arg20)
                    .add(args).build();
            return invoke(env, build.toArray(new AviatorObject[0]));
        }
    }
}
