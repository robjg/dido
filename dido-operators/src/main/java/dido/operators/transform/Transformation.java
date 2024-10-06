package dido.operators.transform;

import dido.data.DidoData;
import dido.data.WriteSchema;

import java.util.function.Function;

/**
 * Transform DidoData into another form of DidoData.
 *
 * @param <D>
 */
public interface Transformation<D extends DidoData> extends Function<DidoData, D> {

    WriteSchema getResultantSchema();

}
