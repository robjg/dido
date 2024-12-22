package dido.how.conversion;

import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.Function;

/**
 * Wrapper for an {@link DidoConversionProvider} that throws an {@link NoConversionException} if
 * a conversion can't found.
 */
public class RequiringConversion {

    private final DidoConversionProvider conversionProvider;

    private RequiringConversion(DidoConversionProvider conversionProvider) {
        this.conversionProvider = Objects.requireNonNull(conversionProvider);
    }

    public class From<F> {

        private final Class<F> from;

        public From(Class<F> from) {
            this.from = from;
        }

        public <T> Function<F, T> to(Class<T> to) {
            Function<F, T> conversion = conversionProvider.conversionFor(from, to);
            if (conversion == null) {
                throw NoConversionException.from(from).to(to);
            }
            else {
                return conversion;
            }
        }
    }

    public static RequiringConversion with(DidoConversionProvider conversionProvider) {
        return new RequiringConversion(conversionProvider);
    }

    public <F> From<F> from(Class<F> from) {
        return new From<>(from);
    }

    public <T> DoubleFunction<T> fromDoubleTo(Class<T> to) {
        DoubleFunction<T> conversion = conversionProvider.fromDoubleTo(to);
        if (conversion == null) {
            throw NoConversionException.from(double.class).to(to);
        }
        else {
            return conversion;
        }
    }

}
