package dido.data.useful;

import dido.data.CompactSchema;

/**
 * Base class providing default implementations of {@link CompactSchema}.
 */
abstract public class AbstractCompactSchema implements CompactSchema {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CompactSchema) {
            return CompactSchema.equals(this, (CompactSchema) obj);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return CompactSchema.hashCode(this);
    }

    @Override
    public String toString() {
        return CompactSchema.toString(this);
    }

}
