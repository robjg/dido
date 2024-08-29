package dido.operators.transform;

import dido.data.DataFactory;
import dido.data.DidoData;

import java.util.function.Consumer;

/**
 * Created by {@link TransformerDefinition}
 */
public interface TransformerFactory {

    Consumer<DidoData> create(DataFactory<?> dataFactory);
}
