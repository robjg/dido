package dido.how.conversion;

import dido.how.util.Primitives;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DefaultConverter implements DidoConverter {

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

    private final Map<Class<?>, Function<Object, ? extends String>> toString;

    private DefaultConverter(Map<Class<?>, Function<? super String, ?>> fromString,
                             Map<Class<?>, Function<Object, ? extends String>> toString) {
        this.fromString = fromString;
        this.toString = toString;
    }

    public static DidoConverter defaultInstance() {
        return new DefaultConverter(conversions, new HashMap<>());
    }

    public static DidoConverter augmentDefaults(Map<Class<?>, Function<? super String, ?>> fromString,
                                         Map<Class<?>, Function<Object, ? extends String>> toString) {

        Map<Class<?>, Function<? super String, ?>> allFromString = new HashMap<>(conversions);
        allFromString.putAll(fromString);

        Map<Class<?>, Function<Object, ? extends String>> allToString = new HashMap<>(toString);

        return new DefaultConverter(allFromString, allToString);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convert(Object from, Class<T> to) {
        if (to == String.class) {
            return (T) convertToString(from);
        }

        if (from instanceof String) {
            return convertFromString((String) from, to);
        }
        if (from == null) {
            return null;
        }

        if (to.isInstance(from)) {
            return (T) from;
        }

        throw new IllegalArgumentException("No Conversion of {" + from + "} to " + to);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertFromString(String string, Class<T> to) {
        if (string == null) {
            return null;
        }
        if (to == String.class) {
            return (T) string;
        }
        if (to.isPrimitive()) {
            to = Primitives.wrap(to);
        }

        Function<? super String, ?> func = fromString.get(to);

        if (func == null) {
            throw new IllegalArgumentException("No Conversion of {" + string + "} to " + to);
        }

        return (T) func.apply(string);
    }

    @Override
    public String convertToString(Object from) {

        if (from == null) {
            return null;
        }

        Function<Object, ? extends String> func = toString.get(from.getClass());

        if (func == null) {
            return from.toString();
        }
        else {
            return func.apply(from);
        }
    }
}
