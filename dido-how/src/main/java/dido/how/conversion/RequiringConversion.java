package dido.how.conversion;

import java.lang.reflect.Type;
import java.util.Objects;
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

        private final Type from;

        public From(Type from) {
            this.from = from;
        }

        public <T> Function<F, T> to(Type to) {
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

    public <F> From<F> from(Type from) {
        return new From<>(from);
    }

}
