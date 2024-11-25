package dido.how.conversion;

import dido.how.util.Primitives;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.*;

/**
 * Simple implementation of {@link DidoConversionProvider}.
 */
public class DefaultConversionProvider implements DidoConversionProvider {

    private static final Map<Class<?>, Function<? super String, ?>> stringConversions = new HashMap<>();

    private static final Map<Class<?>, IntFunction<?>> fromIntConversions = new HashMap<>();

    private static final Map<Class<?>, DoubleFunction<?>> fromDoubleConversions = new HashMap<>();

    private static final Map<Class<?>, LongFunction<?>> fromLongConversions = new HashMap<>();

    static {
        stringConversions.put(Boolean.class, Boolean::valueOf);
        stringConversions.put(Byte.class, Byte::valueOf);
        stringConversions.put(Character.class, s -> s.isEmpty() ? null : s.charAt(0));
        stringConversions.put(Short.class, Short::valueOf);
        stringConversions.put(Integer.class, Integer::valueOf);
        stringConversions.put(Long.class, Long::valueOf);
        stringConversions.put(Float.class, Float::valueOf);
        stringConversions.put(Double.class, Double::valueOf);
        stringConversions.put(Number.class, Double::valueOf);

        fromIntConversions.put(Byte.class, i -> (byte) i);
        fromIntConversions.put(Short.class, i -> (short) i);
        fromIntConversions.put(Integer.class, i -> i);
        fromIntConversions.put(Long.class, i -> (long) i);
        fromIntConversions.put(Float.class, i -> (float) i);
        fromIntConversions.put(Double.class, i -> (double) i);
        fromIntConversions.put(Number.class, i -> i);
        fromIntConversions.put(String.class, Objects::toString);

        fromDoubleConversions.put(Byte.class, d -> (byte) d);
        fromDoubleConversions.put(Short.class, d -> (short) d);
        fromDoubleConversions.put(Integer.class, d -> (int) d);
        fromDoubleConversions.put(Long.class, d -> (long) d);
        fromDoubleConversions.put(Float.class, d -> (float) d);
        fromDoubleConversions.put(Double.class, d -> d);
        fromDoubleConversions.put(Number.class, d -> d);
        fromDoubleConversions.put(String.class, Objects::toString);

        fromLongConversions.put(Byte.class, l -> (byte) l);
        fromLongConversions.put(Short.class, l -> (short) l);
        fromLongConversions.put(Integer.class, l -> (int) l);
        fromLongConversions.put(Long.class, l -> l);
        fromLongConversions.put(Float.class, l -> (float) l);
        fromLongConversions.put(Double.class, l -> (double) l);
        fromLongConversions.put(Number.class, l -> l);
        fromLongConversions.put(String.class, Objects::toString);
    }

    static <T> void registerFromDoubleConversion(Class<T> type, DoubleFunction<T> conversion) {
        fromDoubleConversions.put(type, conversion);
    }


    private final Map<Class<?>, Function<? super String, ?>> fromString;

    private final Map<Class<?>, Function<Object, String>> toString;

    private DefaultConversionProvider(Map<Class<?>, Function<? super String, ?>> fromString,
                                      Map<Class<?>, Function<Object, String>> toString) {
        this.fromString = fromString;
        this.toString = toString;
    }

    public static DidoConversionProvider defaultInstance() {
        return new DefaultConversionProvider(stringConversions, new HashMap<>());
    }

    public static DidoConversionProvider augmentDefaults(Map<Class<?>, Function<? super String, ?>> fromString,
                                                         Map<Class<?>, Function<Object, String>> toString) {

        Map<Class<?>, Function<? super String, ?>> allFromString = new HashMap<>(stringConversions);
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

        if (from == String.class) {
            return (Function<F, T>) convertFromString(to);
        }

        if (to.isAssignableFrom(from)) {
            return value -> (T) value;
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

    @Override
    public <F> ToIntFunction<F> toIntFrom(Class<F> from) {
        if (String.class == from) {
            return string -> Integer.parseInt((String) string);
        }
        else {
            return value -> Integer.parseInt(value.toString());
        }
    }

    @Override
    public <F> ToDoubleFunction<F> toDoubleFrom(Class<F> from) {
        if (String.class == from) {
            return string -> Double.parseDouble((String) string);
        }
        else {
            return value -> Double.parseDouble(value.toString());
        }
    }

    @Override
    public <F> ToLongFunction<F> toLongFrom(Class<F> from) {
        if (String.class == from) {
            return string -> Long.parseLong((String) string);
        }
        else {
            return value -> Long.parseLong(value.toString());
        }
    }

    @Override
    public <T> IntFunction<T> fromIntTo(Class<T> to) {

        @SuppressWarnings("unchecked")
        IntFunction<T> func = (IntFunction<T>) fromIntConversions.get(to);

        if (func == null) {
            throw new IllegalArgumentException("No Conversion of Int to " + to);
        }
        else {
            return func;
        }
    }

    @Override
    public <T> DoubleFunction<T> fromDoubleTo(Class<T> to) {

        @SuppressWarnings("unchecked")
        DoubleFunction<T> func = (DoubleFunction<T>) fromDoubleConversions.get(to);

        if (func == null) {
            throw new IllegalArgumentException("No Conversion of Double to " + to);
        }
        else {
            return func;
        }
    }

    @Override
    public <T> LongFunction<T> fromLongTo(Class<T> to) {

        @SuppressWarnings("unchecked")
        LongFunction<T> func = (LongFunction<T>) fromLongConversions.get(to);

        if (func == null) {
            throw new IllegalArgumentException("No Conversion of Long to " + to);
        }
        else {
            return func;
        }
    }
}
