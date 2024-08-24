package dido.operators.transform;

import dido.data.DidoData;
import dido.data.WritableSchema;

import java.util.function.Function;

/**
 * Transform DidoData into another form of DidoData.
 *
 * @param <D>
 */
public interface Transformation<D extends DidoData> extends Function<DidoData, D> {

    WritableSchema<D> getResultantSchema();

}
