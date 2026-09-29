package dido.data;

import dido.data.schema.HasSchema;

import java.util.function.UnaryOperator;

/**
 * Transform DidoData into another form of DidoData.
 *
 */
public interface DidoTransform extends UnaryOperator<DidoData>, HasSchema {

}
