package dido.oddjob.transform;

import dido.data.DataFactory;
import dido.data.DidoData;

import java.util.function.Consumer;

/**
 * Sets one or more items of data in a {@link DidoData} record, possibly using data from the incoming record.
 *
 */
@FunctionalInterface
public interface Transformer {

    Consumer<DidoData> transform(DataFactory<?> into);
}
