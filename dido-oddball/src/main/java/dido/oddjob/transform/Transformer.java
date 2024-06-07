package dido.oddjob.transform;

import dido.data.DidoData;
import dido.data.GenericData;

/**
 * Sets one or more items of data in a {@link GenericData} record, possibly using data from the incoming record.
 *
 */
@FunctionalInterface
public interface Transformer {

    void transform(DidoData from, DataSetter<String> into);
}
