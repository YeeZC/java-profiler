package me.zyee.java.profiler.theoretical.formula;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public class TheoreticalFormulaTest {
    @Test
    public void testFormula() {
        final Formula<Double> formula = new TheoreticalFormula<>("a * b");
        Assert.assertEquals(10, ((Number) formula.eval(2, 5)).doubleValue(), 10e-6);
        final Formula<Double> spy = spy(formula);
        doThrow(IllegalArgumentException.class).when(spy).eval(notNull());
        boolean success = false;
        try {
            spy.eval("");
        } catch (IllegalArgumentException e) {
            success = true;
        }
        Assert.assertTrue(success);
        verify(spy).eval(notNull());
    }

}