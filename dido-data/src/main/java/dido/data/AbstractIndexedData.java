package dido.data;

/**
 * Base class for {@link IndexedData}. Note that this is preferable to the default methods on
 * the interface because reflection won't recognise the getters as properties, and it's not possible to
 * default the Object methods (toString, hashCode and equals)
 *
 * @param <F> The field type of the schema.
 */
abstract public class AbstractIndexedData<F> implements IndexedData<F> {

    private volatile int hash = 0;

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = IndexedData.hashCode(this);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IndexedData) {
            return IndexedData.equals(this, (IndexedData<?>) obj);
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return IndexedData.toString(this);
    }
}
