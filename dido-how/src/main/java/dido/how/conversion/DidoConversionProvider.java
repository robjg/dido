package dido.how.conversion;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Provide a conversion function between to classes.
 */
public interface DidoConversionProvider {

    <F, T> Function<F, T> conversionFor(Class<F> from , Class<T> to);

    <F> ToIntFunction<F> toIntFrom(Class<F> from);

    <F> ToDoubleFunction<F> toDoubleFrom(Class<F> from);

    <F> ToLongFunction<F> toLongFrom(Class<F> from);
}
