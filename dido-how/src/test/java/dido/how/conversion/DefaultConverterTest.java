package dido.how.conversion;


import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DefaultConverterTest {

    @Test
    void defaultConversions() {

        DidoConverter converter = DefaultConverter.defaultInstance();

        assertThat(converter.convertToString(1), is("1"));
        assertThat(converter.convertFromString("1", int.class), is(1));
        assertThat(converter.convertToString(1.2), is("1.2"));
        assertThat(converter.convertFromString("1.2", double.class), is(1.2));
        assertThat(converter.convertToString(true), is("true"));
        assertThat(converter.convertFromString("true", boolean.class), is(true));
    }

    @Test
    void augmentedConversions() {

        Map<Class<?>, Function<? super String, ?>> fromString = new HashMap<>();
        fromString.put(Instant.class, Instant::parse);

        Map<Class<?>, Function<Object, ? extends String>> toString = new HashMap<>();
        toString.put(Instant.class, instant -> "Whenever");

        DidoConverter converter = DefaultConverter.augmentDefaults(
                fromString, toString);

        assertThat(converter.convertFromString("2023-01-25T19:05:00Z", Instant.class),
                is(Instant.parse("2023-01-25T19:05:00Z")));

        assertThat(converter.convertToString(Instant.parse("2023-01-25T19:05:00Z")),
                is("Whenever"));

    }
}