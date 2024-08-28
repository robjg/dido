package dido.how.conversion;

import java.util.function.Function;

/**
 * Provide a conversion function between to classes.
 */
public interface DidoConversionProvider {

    <F, T> Function<F, T> conversionFor(Class<F> from , Class<T> to);
}
