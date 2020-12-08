package me.zyee.java.profiler.theoretical.formula;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
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

    public static void main(String[] args) {
        final Observable<Integer> first = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                int source = (int) (Math.random() * 5);
                if (source == 3) {
                    System.out.println("find 3");
                    emitter.onNext(source);
                } else if (source == 4) {
                    System.out.println("find 4");
                    emitter.onError(new RuntimeException("4"));
                } else {
                    System.out.println("find other");
                    emitter.onComplete();
                }
            }
        });
        final Observable<Integer> second = Observable.range(6, 5);
        Observable.concat(first, second).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        }, e -> {
            e.printStackTrace();
        });
    }
}