package dido.data.generic;

import dido.data.SchemaFactory;

public interface GenericSchemaFactory<F> extends SchemaFactory {

    GenericSchemaField.Of<F> of();

    GenericSchemaField<F> addGenericSchemaField(GenericSchemaField<F> schemaField);

    GenericDataSchema<F> toSchema();
}
