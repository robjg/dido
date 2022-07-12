package dido.data;

/**
 * Base class for {@link GenericData}. Note that this is preferable to the default methods on
 * the interface because reflection won't recognise the getters as properties, and it's not possible to
 * default the Object methods (toString, hashCode and equals)
 *
 * @param <F> The field type of the schema.
 */
abstract public class AbstractGenericData<F> extends AbstractIndexedData<F> implements GenericData<F> {

    @Override
    public String toString() {
        return GenericData.toString(this);
    }

}
