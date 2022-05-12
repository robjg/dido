package dido.data;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class OptionalPerformanceMain {

    static class Foo {

        private final String foo;

        Foo(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }

        public Optional<String> getOptionalFoo() {
            return Optional.ofNullable(foo);
        }
    }

    static int countWithOptional(Foo[] lots) {
        int count = 0;
        for (Foo lot : lots) {
            if (lot.getOptionalFoo().isPresent()) {
                ++count;
            }
        }
        return count;
    }

    static int countWithoutOptional(Foo[] lots) {
        int count = 0;
        for (Foo lot : lots) {
            if (lot.getFoo() != null) {
                ++count;
            }
        }
        return count;
    }

    static Foo[] getLots() {

        Foo[] lots = new Foo[10_000_000];
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < lots.length; ++i) {
            lots[i] = new Foo(random.nextBoolean() ? "Foo" : null);
        }

        return lots;
    }

    static Results time(Foo[] lots, Function<Foo[], Integer> runnable) {
        Instant before = Instant.now();
        int count = runnable.apply(lots);
        Instant after = Instant.now();
        return new Results(count, Duration.between(before, after).toNanos());
    }

    static class Results {
        final int count;
        final long nanos;

        Results(int count, long nanos) {
            this.count = count;
            this.nanos = nanos;
        }

        @Override
        public String toString() {
            return "Count: " + count + ", Nanos: " + String.format("%,d", nanos);
        }
    }

    public static void main(String[] args) {

        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));

        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));

        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));

        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Without:  " + time(getLots(), OptionalPerformanceMain::countWithoutOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
        System.out.println("Optional: " + time(getLots(), OptionalPerformanceMain::countWithOptional));
    }
}
