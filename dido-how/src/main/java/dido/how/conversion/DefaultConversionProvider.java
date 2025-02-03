package dido.how.conversion;

import dido.data.util.TypeUtil;
import dido.how.util.Primitives;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Simple implementation of {@link DidoConversionProvider}.
 */
public class DefaultConversionProvider implements DidoConversionProvider {

    private static final Map<Class<?>, Function<? super String, ?>> defaultStringConversions = new HashMap<>();

    private static final Map<Class<?>, Function<? super Number, ?>> defaultNumberConversions = new HashMap<>();

    static {

        defaultStringConversions.put(Boolean.class, Boolean::valueOf);
        defaultStringConversions.put(Byte.class, Byte::valueOf);
        defaultStringConversions.put(Character.class, s -> s.isEmpty() ? null : s.charAt(0));
        defaultStringConversions.put(Short.class, Short::valueOf);
        defaultStringConversions.put(Integer.class, Integer::valueOf);
        defaultStringConversions.put(Long.class, Long::valueOf);
        defaultStringConversions.put(Float.class, Float::valueOf);
        defaultStringConversions.put(Double.class, Double::valueOf);
        defaultStringConversions.put(Number.class, Double::valueOf);

        defaultNumberConversions.put(Byte.class, Number::byteValue);
        defaultNumberConversions.put(Short.class, Number::shortValue);
        defaultNumberConversions.put(Integer.class, Number::intValue);
        defaultNumberConversions.put(Long.class, Number::longValue);
        defaultNumberConversions.put(Float.class, Number::floatValue);
        defaultNumberConversions.put(Double.class, Number::doubleValue);
    }

    private final Map<Type, From<?>> conversions;

    private DefaultConversionProvider(Builder builder) {
        this.conversions = new HashMap<>(builder.conversions);
    }

    public static class Builder {

        private final Map<Type,  From<?>> conversions = new HashMap<>();

        public <F, T> Builder conversion(Type from, Type to, Function<? super F, ? extends T> conversion) {

            Type from_ = Primitives.wrap(from);
            Type to_ = Primitives.wrap(to);

            //noinspection unchecked
            ((From<T>) conversions.computeIfAbsent(to_, k -> new From<>()))
                    .add(from_, conversion);
            return this;
        }

        public DidoConversionProvider make() {
            return new DefaultConversionProvider(this);
        }
    }

    public static Builder with() {
        Builder builder = new Builder();
        builder.conversion(Object.class, String.class, Objects::toString);
        defaultStringConversions.forEach((key, value) -> builder.conversion(String.class, key, value));
        defaultNumberConversions.forEach((key, value) -> builder.conversion(Number.class, key, value));
        return builder;
    }

    public static DidoConversionProvider defaultInstance() {
        return with().make();
    }


    @SuppressWarnings("unchecked")
    @Override
    public <F, T> Function<F, T> conversionFor(Type from, Type to) {

        Type from_ = Primitives.wrap(from);
        Type to_ = Primitives.wrap(to);

        if (TypeUtil.isAssignableFrom(to_, from_)) {
            return value -> (T) value;
        }

        From<T> froms = (From<T>) conversions.get(to_);

        if (froms == null) {
            return null;
        }
        else {
            return froms.findConversion(from_);
        }
    }

    static class From<T> {

        private final Map<Type, Function<?, ? extends T>> conversions = new HashMap<>();

        <F> void add(Type from, Function<? super F, ? extends T> conversion) {
            conversions.put(from, conversion);
        }

        <F> Function<F, T> findConversion(Type from) {

            if (from == null) {
                return  null;
            }

            Function<?, ? extends T> conversion = conversions.get(from);
            if (conversion == null) {
                conversion = findConversion(superClass(from));
            }
            if (conversion == null) {
                for (Class<?> inter : interfaces(from)) {
                    conversion = findConversion(superClass(inter));
                    if (conversion != null) {
                        break;
                    }
                }
            }

            if (conversion == null) {
                return null;
            }

            //noinspection unchecked
            return (Function<F, T>) conversion;
        }
    }

    static Class<?> superClass(Type type) {

        if (type instanceof Class) {
            return ((Class<?>) type).getSuperclass();
        }
        else if (type instanceof ParameterizedType){
            return superClass(((ParameterizedType) type).getRawType());
        }
        else {
            return null;
        }
    }

    static Class<?>[] interfaces(Type type) {

        if (type instanceof Class) {
            return ((Class<?>) type).getInterfaces();
        }
        else if (type instanceof ParameterizedType){
            return interfaces(((ParameterizedType) type).getRawType());
        }
        else {
            return null;
        }
    }
}
