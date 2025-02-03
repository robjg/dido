package dido.how.conversion;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public class ConversionPerformanceTest {

    public static final int SAMPLE_SIZE = 50; // _000_000;


    double[] da = new double[SAMPLE_SIZE];
    String[] sa = new String[SAMPLE_SIZE];

    {
        Random random = new Random();
        for (int i = 0; i < da.length; i++) {
            da[i] = random.nextDouble();
        }
    }


    @Test
    void performance() {

        test(new ToStringWrapper());
        test(new FromStringWrapper());
        test(new ToStringPrimitive());
        test(new FromStringPrimitive());
        test(new ToStringWrapper());
        test(new FromStringWrapper());
        test(new ToStringPrimitive());
        test(new FromStringPrimitive());
        test(new ToStringWrapper());
        test(new FromStringWrapper());
    }

    void test(Runnable task) {
        System.out.println(time(task) + " " + task.getClass().getSimpleName());
    }

    static long time(Runnable task) {
        System.gc();
        Instant before = Instant.now();
        task.run();
        Instant after = Instant.now();
        return Duration.between(before, after).toNanos();
    }

    class ToStringPrimitive implements Runnable {

        @Override
        public void run() {

            DoubleFunction<String> func = Double::toString;

            for (int i = 0; i < sa.length; i++) {
                sa[i] = func.apply(da[i]);
            }
        }
    }

    class FromStringPrimitive implements Runnable {

        @Override
        public void run() {

            ToDoubleFunction<String> func = Double::parseDouble;

            for (int i = 0; i < sa.length; i++) {
                da[i] = func.applyAsDouble(sa[i]);
            }
        }
    }

    class ToStringWrapper implements Runnable {

        @Override
        public void run() {

            Function<Double, String> func = Object::toString;

            for (int i = 0; i < sa.length; i++) {
                sa[i] = func.apply(da[i]);
            }
        }
    }

    class FromStringWrapper implements Runnable {

        @Override
        public void run() {

            Function<String, Double> func = Double::valueOf;

            for (int i = 0; i < sa.length; i++) {
                da[i] = func.apply(sa[i]);
            }
        }
    }

}
