package me.zyee.java.profiler.theoretical.formula;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public class TheoreticalFormula<T> implements Formula<T> {
    private final Expression expression;

    public TheoreticalFormula(String formula) {
        this.expression = AviatorEvaluator.compile(formula, true);
    }

    @Override
    public T eval(Object... args) {
        List<String> names = expression.getVariableNames();
        int length = args == null ? 0 : args.length;
        if (length == names.size()) {
            final Map<String, Object> params = IntStream.range(0, length)
                    .boxed()
                    .collect(Collectors.toMap(names::get, i -> args[i]));
            return (T) expression.execute(params);
        }
        throw new IllegalArgumentException();
    }
}
