package dido.how.conversion;


import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class DefaultConversionProviderTest {

    @Test
    void defaultConversions() {

        DidoConversionProvider conversionProvider = DefaultConversionProvider.defaultInstance();

        assertThat(conversionProvider.conversionFor(int.class, String.class).apply(1), is("1"));
        assertThat(conversionProvider.conversionFor(String.class, int.class).apply("1"), is(1));
        assertThat(conversionProvider.conversionFor(Integer.class, String.class).apply(1), is("1"));
        assertThat(conversionProvider.conversionFor(String.class, Integer.class).apply("1"), is(1));

        assertThat(conversionProvider.conversionFor(long.class, String.class).apply(1L), is("1"));
        assertThat(conversionProvider.conversionFor(String.class, long.class).apply("1"), is(1L));

        assertThat(conversionProvider.conversionFor(double.class, String.class).apply(1.2), is("1.2"));
        assertThat(conversionProvider.conversionFor(String.class, double.class).apply("1.2"), is(1.2));

        assertThat(conversionProvider.conversionFor(boolean.class, String.class).apply(true), is("true"));
        assertThat(conversionProvider.conversionFor(String.class, boolean.class).apply("true"), is(true));

        assertThat(conversionProvider.conversionFor(int.class, long.class).apply(1), is(1L));

        Function<Long, Integer> func = conversionProvider.conversionFor(long.class, int.class);

        assertThat(func.apply(Long.valueOf(1L)), is(Integer.valueOf(1)));

    }

    @Test
    void augmentedConversions() {

        DidoConversionProvider conversionProvider = DefaultConversionProvider.with()
                .conversion(String.class, Instant.class, Instant::parse)
                .conversion(Instant.class, String.class, instant -> "Whenever")
                .make();

        assertThat(conversionProvider.conversionFor(String.class, Instant.class).apply("2023-01-25T19:05:00Z"),
                is(Instant.parse("2023-01-25T19:05:00Z")));

        assertThat(conversionProvider.conversionFor(Instant.class, String.class)
                        .apply(Instant.parse("2023-01-25T19:05:00Z")),
                is("Whenever"));
    }

    @Test
    void defaultFromPrimitiveConversions() {

        DidoConversionProvider conversionProvider = DefaultConversionProvider.with()
                .make();

        assertThat(conversionProvider.conversionFor(int.class, String.class).apply(42),
                is("42"));

        assertThat(conversionProvider.conversionFor(int.class, double.class).apply(42),
                is(42.0));

        assertThat(conversionProvider.conversionFor(long.class, String.class).apply(1000L),
                is("1000"));

        assertThat(conversionProvider.conversionFor(double.class, String.class).apply(12.34),
                is("12.34"));
    }

    @Test
    void registeredFromPrimitiveConversions() {

        DidoConversionProvider conversionProvider = DefaultConversionProvider.with()
                .<Integer, LocalDate>conversion(int.class, LocalDate.class,
                        i -> LocalDate.parse(Integer.toString(i),
                                DateTimeFormatter.ofPattern("yyyyMMdd")))
                .conversion(long.class, Instant.class,
                        Instant::ofEpochMilli)
                .<Double, BigDecimal>conversion(double.class, BigDecimal.class, BigDecimal::valueOf)
                .make();

        assertThat(conversionProvider.conversionFor(int.class, LocalDate.class).apply(20250129),
                is(LocalDate.parse("2025-01-29")));

        assertThat(conversionProvider.conversionFor(long.class, Instant.class).apply(1000L),
                is(Instant.parse("1970-01-01T00:00:01Z")));

        assertThat(conversionProvider.conversionFor(double.class, BigDecimal.class).apply(12.34),
                is(BigDecimal.valueOf(12.34)));
    }

    @Test
    void testNoConversionRequired() {

        DidoConversionProvider conversionProvider = DefaultConversionProvider.defaultInstance();

        Function<Double, Double> conversion1 = conversionProvider.conversionFor(Double.class, Double.class);

        assertThat(conversion1.apply(12.4), is((12.4)));

        Function<Double, Number> conversion2 = conversionProvider.conversionFor(Double.class, Number.class);

        assertThat(conversion2.apply(12.4), is((12.4)));

        Function<Double, Double> conversion3 = conversionProvider.conversionFor(double.class, Double.class);

        assertThat(conversion3.apply(12.4), is((12.4)));

        Function<Double, Double> conversion4 = conversionProvider.conversionFor(Double.class, double.class);

        assertThat(conversion4.apply(12.4), is((12.4)));
    }

    @Test
    void testConvertGenericType() {

        Type toType = new TypeToken<List<String>>() {
        }.getType();

        DidoConversionProvider conversionProvider = DefaultConversionProvider.with()
                .conversion(String[].class, toType,
                        (String[] stringArray) -> Arrays.stream(stringArray).collect(Collectors.toList()))
                .make();

        Function<String[], List<String>> func = conversionProvider.conversionFor(String[].class, toType);

        assertThat(func.apply(new String[]{"red", "yellow", "blue"}), contains("red", "yellow", "blue"));
    }

}