package dido.how.conversion;

import java.util.function.*;

/**
 * Provide a conversion function between to classes.
 */
public interface DidoConversionProvider {

    <F, T> Function<F, T> conversionFor(Class<F> from , Class<T> to);

    <F> ToIntFunction<F> toIntFrom(Class<F> from);

    <F> ToDoubleFunction<F> toDoubleFrom(Class<F> from);

    <F> ToLongFunction<F> toLongFrom(Class<F> from);

    <T> IntFunction<T> fromIntTo(Class<T> to);

    <T> DoubleFunction<T> fromDoubleTo(Class<T> to);

    <T> LongFunction<T> fromLongTo(Class<T> to);
}
