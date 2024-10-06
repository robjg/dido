package dido.data.generic;

import dido.data.WriteSchemaFactory;

public interface GenericWriteSchemaFactory<F>
        extends GenericSchemaFactory<F>, WriteSchemaFactory {

    @Override
    GenericWriteSchema<F> toSchema();
}
