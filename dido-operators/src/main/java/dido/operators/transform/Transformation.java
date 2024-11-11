package dido.operators.transform;

import dido.data.DataSchema;
import dido.data.DidoData;

import java.util.function.Function;

/**
 * Transform DidoData into another form of DidoData.
 *
 */
public interface Transformation extends Function<DidoData, DidoData> {

    DataSchema getResultantSchema();

}
