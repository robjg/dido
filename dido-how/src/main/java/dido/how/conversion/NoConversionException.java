package dido.how.conversion;

import dido.how.DataException;

/**
 * Thrown when a Conversion can't be found.
 *
 * @see RequiringConversion
 */
public class NoConversionException extends DataException {

    protected NoConversionException(String message) {
        super(message);
    }

    public static class From {

        private final Class<?> from;

        public From(Class<?> from) {
            this.from = from;
        }

        public NoConversionException to(Class<?> to) {
            return new NoConversionException("No conversion from " + from + " to " + to);
        }
    }

    public static From from(Class<?> from) {
        return new From(from);
    }
}
