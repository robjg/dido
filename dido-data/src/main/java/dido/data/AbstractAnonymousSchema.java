package dido.data;

/**
 * Base class providing default implementations of {@link AnonymousSchema}.
 */
abstract public class AbstractAnonymousSchema implements AnonymousSchema {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnonymousSchema) {
            return AnonymousSchema.equals(this, (AnonymousSchema) obj);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return AnonymousSchema.hashCode(this);
    }

    @Override
    public String toString() {
        return AnonymousSchema.toString(this);
    }

}
