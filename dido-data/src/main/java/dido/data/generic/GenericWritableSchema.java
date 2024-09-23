package dido.data.generic;

import dido.data.WritableSchema;

public interface GenericWritableSchema<F, D extends GenericData<F>>
        extends GenericReadableSchema<F>, WritableSchema<D> {

    GenericWritableSchemaFactory<F, D> newSchemaFactory();

    GenericDataFactory<F, D> newDataFactory();

}
