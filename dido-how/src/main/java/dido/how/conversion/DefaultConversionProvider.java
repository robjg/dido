package dido.how.conversion;

import dido.how.util.Primitives;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Simple implementation of {@link DidoConversionProvider}.
 */
public class DefaultConversionProvider implements DidoConversionProvider {

    private static final Map<Class<?>, Function<? super String, ?>> conversions = new HashMap<>();

    static {
        conversions.put(Boolean.class, Boolean::valueOf);
        conversions.put(Byte.class, Byte::valueOf);
        conversions.put(Character.class, s -> s.isEmpty() ? null : s.charAt(0));
        conversions.put(Short.class, Short::valueOf);
        conversions.put(Integer.class, Integer::valueOf);
        conversions.put(Long.class, Long::valueOf);
        conversions.put(Float.class, Float::valueOf);
        conversions.put(Double.class, Double::valueOf);
        conversions.put(Number.class, Double::valueOf);
    }

    private final Map<Class<?>, Function<? super String, ?>> fromString;

    private final Map<Class<?>, Function<Object, String>> toString;

    private DefaultConversionProvider(Map<Class<?>, Function<? super String, ?>> fromString,
                                      Map<Class<?>, Function<Object, String>> toString) {
        this.fromString = fromString;
        this.toString = toString;
    }

    public static DidoConversionProvider defaultInstance() {
        return new DefaultConversionProvider(conversions, new HashMap<>());
    }

    public static DidoConversionProvider augmentDefaults(Map<Class<?>, Function<? super String, ?>> fromString,
                                                         Map<Class<?>, Function<Object, String>> toString) {

        Map<Class<?>, Function<? super String, ?>> allFromString = new HashMap<>(conversions);
        allFromString.putAll(fromString);

        Map<Class<?>, Function<Object, String>> allToString = new HashMap<>(toString);

        return new DefaultConversionProvider(allFromString, allToString);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <F, T> Function<F, T> conversionFor(Class<F> from, Class<T> to) {

        if (to == String.class) {
            return (Function<F, T>) convertToString(from);
        }

        if (from.isAssignableFrom(String.class)) {
            return (Function<F, T>) convertFromString(to);
        }

        if (to.isAssignableFrom(from)) {
            return f -> (T) from;
        }

        throw new IllegalArgumentException("No Conversion of {" + from + "} to " + to);
    }

    @SuppressWarnings("unchecked")
    public <T> Function<String, T> convertFromString(Class<T> to) {

        if (to == String.class) {
            return string -> (T) string;
        }

        if (to.isPrimitive()) {
            to = Primitives.wrap(to);
        }

        Function<String, T> func = (Function<String, T>) fromString.get(to);

        if (func == null) {
            throw new IllegalArgumentException("No Conversion of String to " + to);
        }

        return string -> {
            if (string == null) {
                return null;
            }
            else {
                return func.apply(string);
            }
        };
    }

    public <F> Function<F, String> convertToString(Class<F> from) {

        @SuppressWarnings("unchecked")
        Function<F, String> func = (Function<F, String>) toString.get(from);

        return Objects.requireNonNullElseGet(func, () -> Object::toString);
    }
}
