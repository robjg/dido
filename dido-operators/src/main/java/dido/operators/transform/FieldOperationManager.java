package dido.operators.transform;

import dido.data.SchemaField;

public interface FieldOperationManager {

    void addOperation(FieldOperationFactory fieldOperationFactory, SchemaField... schemaFields);

    void removeField(SchemaField schemaField);

}
