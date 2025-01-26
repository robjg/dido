package dido.data.useful;

import dido.data.CompactData;
import dido.data.IndexedData;

/**
 * Base class providing default implementations for {@link CompactData}. Implementations
 * need only implement {@link IndexedData#getAt(int)} and {@link CompactData#getSchema()}.
 */
abstract public class AbstractCompactData extends AbstractIndexedData
        implements CompactData {

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
            return false;
    }

    @Override
    public String toString() {
        return CompactData.toString(this);
    }

}
