package dido.data;

/**
 * Something that allows access to its underlying data.
 *
 * @param <T> The type of the wrapped data.
 */
public interface DataWrapper<T> {

    Class<T> getWrappedDataType();

    T getWrappedData();
}
