package dido.data.operators;

import dido.data.SchemaField;

public interface FieldOperationManager {

    void addOperation(SchemaField schemaField, FieldOperationFactory fieldOperationFactory);

    void removeField(SchemaField schemaField);

}
