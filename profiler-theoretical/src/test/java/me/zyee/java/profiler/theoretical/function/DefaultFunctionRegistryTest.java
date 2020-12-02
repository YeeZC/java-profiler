package me.zyee.java.profiler.theoretical.function;

import me.zyee.java.profiler.theoretical.formula.TheoreticalFormula;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultFunctionRegistryTest {
    @Spy
    private DefaultFunctionRegistry registry;

    @Test
    public void register() {
        final Function<String> mock = mock(Function.class);
        when(mock.getName()).thenReturn("abc");
        when(mock.getParameterCount()).thenReturn(1);
        when(mock.eval(eq("hello"))).thenReturn("Hello world");
        registry.register(mock);
        final TheoreticalFormula<String> formula = new TheoreticalFormula<>("abc(supplier)");
        Assert.assertEquals("Hello world", formula.eval("hello"));
        verify(mock).getName();
        verify(mock).getParameterCount();
    }

}