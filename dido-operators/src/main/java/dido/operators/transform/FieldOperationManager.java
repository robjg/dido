package dido.operators.transform;

import dido.data.SchemaField;

public interface FieldOperationManager {

    void addOperation(TransformerFactory transformerFactory, SchemaField... schemaFields);

    void removeField(SchemaField schemaField);

}
