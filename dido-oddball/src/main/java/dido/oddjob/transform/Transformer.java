package dido.oddjob.transform;

import dido.data.GenericData;

/**
 * Sets one or more items of data in a {@link GenericData} record, possibly using data from the incoming record.
 *
 * @param <F> The field type of the incoming record
 * @param <T> The field type of the outgoing data.
 */
@FunctionalInterface
public interface Transformer<F, T> {

    void transform(GenericData<F> from, DataSetter<T> into);
}
