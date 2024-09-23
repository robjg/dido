package dido.data.generic;

import dido.data.WritableSchemaFactory;

public interface GenericWritableSchemaFactory<F, D extends GenericData<F>>
        extends GenericSchemaFactory<F>, WritableSchemaFactory<D> {

    @Override
    GenericWritableSchema<F, D> toSchema();
}
