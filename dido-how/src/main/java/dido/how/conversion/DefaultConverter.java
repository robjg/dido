package dido.how.conversion;

import dido.how.util.Primitives;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class DefaultConverter implements DidoConverter {

    private static final Map<Class<?>, Function<String, Object>> conversions = new HashMap<>();

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


    @SuppressWarnings("unchecked")
    @Override
    public <T> T convert(Object thing, Class<T> type) {
        if (type == String.class) {
            return (T) convertToString(thing);
        }

        if (thing instanceof String) {
            return convertFromString((String) thing, type);
        }
        if (thing == null) {
            return null;
        }

        if (type.isInstance(thing)) {
            return (T) thing;
        }

        throw new IllegalArgumentException("No Conversion of {" + thing + "} to " + type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertFromString(String string, Class<T> type) {
        if (string == null) {
            return null;
        }
        if (type == String.class) {
            return (T) string;
        }
        if (type.isPrimitive()) {
            type = Primitives.wrap(type);
        }

        Function<String, Object> func = conversions.get(type);

        if (func == null) {
            throw new IllegalArgumentException("No Conversion of {" + string + "} to " + type);
        }

        return (T) func.apply(string);
    }

    @Override
    public String convertToString(Object thing) {
        return Objects.toString(thing);
    }
}
