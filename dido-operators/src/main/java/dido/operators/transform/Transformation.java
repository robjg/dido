package dido.operators.transform;

import dido.data.DataSchema;
import dido.data.DidoData;

import java.util.function.Function;

/**
 * Transform DidoData into another form of DidoData.
 *
 * @param <D>
 */
public interface Transformation<D extends DidoData> extends Function<DidoData, D> {

    DataSchema getResultantSchema();

}
