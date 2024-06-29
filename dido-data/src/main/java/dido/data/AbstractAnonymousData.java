package dido.data;

/**
 * Base class providing default implementations for {@link AnonymousData}. Implementations
 * need only implement {@link IndexedData#getAt(int)} and {@link AnonymousData#getSchema()}.
 */
abstract public class AbstractAnonymousData extends AbstractIndexedData
        implements AnonymousData {

    private volatile int hash = -1;

    @Override
    public int hashCode() {
        if (hash == -1) {
            hash = AnonymousData.hashCode(this);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnonymousData) {
            return AnonymousData.equals(this, (AnonymousData) obj);
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return AnonymousData.toString(this);
    }

}
