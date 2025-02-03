package dido.how.conversion;

import dido.how.DataException;

import java.lang.reflect.Type;

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

        private final Type from;

        public From(Type from) {
            this.from = from;
        }

        public NoConversionException to(Type to) {
            return new NoConversionException("No conversion from " + from.getTypeName()
                    + " to " + to.getTypeName());
        }
    }

    public static From from(Type from) {
        return new From(from);
    }
}
