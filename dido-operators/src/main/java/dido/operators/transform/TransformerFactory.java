package dido.operators.transform;

import dido.data.DidoData;
import dido.data.WritableData;
import dido.data.WriteSchema;

import java.util.function.BiConsumer;

/**
 * Created by {@link TransformerDefinition}
 */
@FunctionalInterface
public interface TransformerFactory {

    BiConsumer<DidoData, WritableData> create(WriteSchema writableData);
}
