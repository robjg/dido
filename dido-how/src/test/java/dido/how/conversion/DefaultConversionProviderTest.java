package dido.how.conversion;


import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DefaultConversionProviderTest {

    @Test
    void defaultConversions() {

        DidoConversionProvider conversionProvider = DefaultConversionProvider.defaultInstance();

        assertThat(conversionProvider.conversionFor(int.class, String.class).apply(1), is("1"));
        assertThat(conversionProvider.conversionFor(String.class, int.class).apply("1"), is(1));
        assertThat(conversionProvider.conversionFor(double.class, String.class).apply(1.2), is("1.2"));
        assertThat(conversionProvider.conversionFor(String.class, double.class).apply("1.2"), is(1.2));
        assertThat(conversionProvider.conversionFor(boolean.class, String.class).apply(true), is("true"));
        assertThat(conversionProvider.conversionFor(String.class, boolean.class).apply("true"), is(true));
    }

    @Test
    void augmentedConversions() {

        Map<Class<?>, Function<? super String, ?>> fromString = new HashMap<>();
        fromString.put(Instant.class, Instant::parse);

        Map<Class<?>, Function<Object, String>> toString = new HashMap<>();
        toString.put(Instant.class, instant -> "Whenever");

        DidoConversionProvider conversionProvider = DefaultConversionProvider.augmentDefaults(
                fromString, toString);

        assertThat(conversionProvider.conversionFor(String.class, Instant.class).apply("2023-01-25T19:05:00Z"),
                is(Instant.parse("2023-01-25T19:05:00Z")));

        assertThat(conversionProvider.conversionFor(Instant.class, String.class)
                        .apply(Instant.parse("2023-01-25T19:05:00Z")),
                is("Whenever"));
    }

    @Test
    void testNoConversionRequired() {

        DidoConversionProvider conversionProvider = DefaultConversionProvider.defaultInstance();

        Function<Double, Double> conversion1 = conversionProvider.conversionFor(Double.class, Double.class);

        assertThat(conversion1.apply(12.4), Matchers.is((12.4)));

        Function<Double, Number> conversion2 = conversionProvider.conversionFor(Double.class, Number.class);

        assertThat(conversion2.apply(12.4), Matchers.is((12.4)));

        assertThat(Assertions.assertThrows(IllegalArgumentException.class,
                        () -> conversionProvider.conversionFor(Object.class, Double.class))
                .getMessage(), Matchers.containsString("No Conversion"));


    }

}