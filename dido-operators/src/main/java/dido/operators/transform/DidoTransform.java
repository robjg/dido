package dido.operators.transform;

import dido.data.DataSchema;
import dido.data.DidoData;

import java.util.function.UnaryOperator;

/**
 * Transform DidoData into another form of DidoData.
 *
 */
public interface DidoTransform extends UnaryOperator<DidoData> {

    DataSchema getResultantSchema();

}
