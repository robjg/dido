package dido.how.conversion;

import dido.how.util.Primitives;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.*;

/**
 * Simple implementation of {@link DidoConversionProvider}.
 */
public class DefaultConversionProvider implements DidoConversionProvider {

    private static final Map<Class<?>, Function<? super String, ?>> defaultStringConversions = new HashMap<>();

    private static final Map<Class<?>, IntFunction<?>> defaultFromIntConversions = new HashMap<>();

    private static final Map<Class<?>, DoubleFunction<?>> defaultFromDoubleConversions = new HashMap<>();

    private static final Map<Class<?>, LongFunction<?>> defaultFromLongConversions = new HashMap<>();

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

        defaultFromIntConversions.put(Byte.class, i -> (byte) i);
        defaultFromIntConversions.put(Short.class, i -> (short) i);
        defaultFromIntConversions.put(Integer.class, i -> i);
        defaultFromIntConversions.put(Long.class, i -> (long) i);
        defaultFromIntConversions.put(Float.class, i -> (float) i);
        defaultFromIntConversions.put(Double.class, i -> (double) i);
        defaultFromIntConversions.put(Number.class, i -> i);
        defaultFromIntConversions.put(String.class, Objects::toString);

        defaultFromDoubleConversions.put(Byte.class, d -> (byte) d);
        defaultFromDoubleConversions.put(Short.class, d -> (short) d);
        defaultFromDoubleConversions.put(Integer.class, d -> (int) d);
        defaultFromDoubleConversions.put(Long.class, d -> (long) d);
        defaultFromDoubleConversions.put(Float.class, d -> (float) d);
        defaultFromDoubleConversions.put(Double.class, d -> d);
        defaultFromDoubleConversions.put(Number.class, d -> d);
        defaultFromDoubleConversions.put(String.class, Objects::toString);

        defaultFromLongConversions.put(Byte.class, l -> (byte) l);
        defaultFromLongConversions.put(Short.class, l -> (short) l);
        defaultFromLongConversions.put(Integer.class, l -> (int) l);
        defaultFromLongConversions.put(Long.class, l -> l);
        defaultFromLongConversions.put(Float.class, l -> (float) l);
        defaultFromLongConversions.put(Double.class, l -> (double) l);
        defaultFromLongConversions.put(Number.class, l -> l);
        defaultFromLongConversions.put(String.class, Objects::toString);
    }

    private final Map<Type, From<?>> conversions;

    private final Map<Type, IntFunction<?>> fromIntConversions;

    private final Map<Type, DoubleFunction<?>> fromDoubleConversions;

    private final Map<Type, LongFunction<?>> fromLongConversions;

    private DefaultConversionProvider(Builder builder) {
        this.conversions = new HashMap<>(builder.conversions);
        this.fromIntConversions = new HashMap<>(builder.fromIntConversions);
        this.fromDoubleConversions = new HashMap<>(builder.fromDoubleConversions);
        this.fromLongConversions = new HashMap<>(builder.fromLongConversions);
    }

    public static class Builder {

        private final Map<Type,  From<?>> conversions = new HashMap<>();

        private final Map<Type, IntFunction<?>> fromIntConversions = new HashMap<>();

        private final Map<Type, DoubleFunction<?>> fromDoubleConversions = new HashMap<>();

        private final Map<Type, LongFunction<?>> fromLongConversions = new HashMap<>();


        public <F, T> Builder conversion(Type from, Type to, Function<? super F, ? extends T> conversion) {
            ((From<T>)conversions.computeIfAbsent(to, k -> new From<>()))
                    .add(from, conversion);
            return this;
        }

        public <T> Builder doubleConversion(Type to, DoubleFunction<? extends T> conversion) {
            fromDoubleConversions.put(to, conversion);
            return conversion(Double.class, to, (Function<Double, T>) d -> conversion.apply(d));
        }

        public <T> Builder intConversion(Type to, IntFunction<? extends T> conversion) {
            fromIntConversions.put(to, conversion);
            return conversion(Integer.class, to, (Function<Integer, T>) i -> conversion.apply(i));
        }

        public <T> Builder longConversion(Type to, LongFunction<? extends T> conversion) {
            fromLongConversions.put(to, conversion);
            return conversion(Long.class, to, (Function<Long, T>) l -> conversion.apply(l));
        }

        public DidoConversionProvider make() {
            return new DefaultConversionProvider(this);
        }
    }

    public static Builder with() {
        Builder builder = new Builder();
        builder.conversion(Object.class, String.class, Objects::toString);
        defaultStringConversions.entrySet().forEach(entry -> builder.conversion(String.class, entry.getKey(), entry.getValue()));
        defaultFromIntConversions.forEach((key, value) -> builder.intConversion(key, (IntFunction) value));
        defaultFromDoubleConversions.forEach((key, value)-> builder.doubleConversion(key, (DoubleFunction) value));
        defaultFromLongConversions.forEach((key, value) -> builder.longConversion(key, (LongFunction) value));
        return builder;
    }

    public static DidoConversionProvider defaultInstance() {
        return with().make();
    }


    @SuppressWarnings("unchecked")
    @Override
    public <F, T> Function<F, T> conversionFor(Class<F> from, Class<T> to) {

        Class<?> from_ = (Class<?>) Primitives.wrap(from);
        Class<?> to_ = (Class<?>) Primitives.wrap(to);

        if (to_.isAssignableFrom(from_)) {
            return value -> (T) value;
        }

        From<T> froms = (From<T>) conversions.get(to_);

        Function<F, T> conversion = null;

        if (froms != null) {
            conversion = froms.findConversion(from_);
        }

        return conversion;
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
        IntFunction<T> func = (IntFunction<T>) defaultFromIntConversions.get(to);

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
        DoubleFunction<T> func = (DoubleFunction<T>) defaultFromDoubleConversions.get(to);

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
        LongFunction<T> func = (LongFunction<T>) defaultFromLongConversions.get(to);

        if (func == null) {
            throw new IllegalArgumentException("No Conversion of Long to " + to);
        }
        else {
            return func;
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
