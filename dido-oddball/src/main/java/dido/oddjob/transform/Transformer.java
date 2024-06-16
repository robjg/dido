package dido.oddjob.transform;

import dido.data.DidoData;

/**
 * Sets one or more items of data in a {@link DidoData} record, possibly using data from the incoming record.
 *
 */
@FunctionalInterface
public interface Transformer {

    void transform(DidoData from, DataSetter into);
}
