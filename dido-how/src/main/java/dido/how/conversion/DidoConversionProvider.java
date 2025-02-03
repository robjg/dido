package dido.how.conversion;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Provide a conversion function between to classes.
 */
public interface DidoConversionProvider {

    <F, T> Function<F, T> conversionFor(Type from , Type to);

}
