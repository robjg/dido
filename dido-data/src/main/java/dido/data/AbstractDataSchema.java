package dido.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base class providing default implementations of {@link DataSchema}.
 */
public abstract class AbstractDataSchema implements DataSchema {

    @Override
    public String getFieldNameAt(int index) {
        SchemaField schemaField = getSchemaFieldAt(index);
        return schemaField == null ? null : schemaField.getName();
    }

    @Override
    public Class<?> getTypeAt(int index) {
        SchemaField schemaField = getSchemaFieldAt(index);
        return schemaField == null ? null : schemaField.getType();
    }

    @Override
    public DataSchema getSchemaAt(int index) {
        SchemaField schemaField = getSchemaFieldAt(index);
        return schemaField == null ? null : schemaField.getNestedSchema();
    }

    @Override
    public int getIndexNamed(String name) {
        SchemaField schemaField = getSchemaFieldNamed(name);
        return schemaField == null ? 0 : schemaField.getIndex();
    }

    @Override
    public Class<?> getTypeNamed(String name) {
        SchemaField schemaField = getSchemaFieldNamed(name);
        return schemaField == null ? null : schemaField.getType();
    }

    @Override
    public DataSchema getSchemaNamed(String name) {
        SchemaField schemaField = getSchemaFieldNamed(name);
        return schemaField == null ? null : schemaField.getNestedSchema();
    }

    @Override
    public Collection<String> getFieldNames() {
        List<String> fields = new ArrayList<>(lastIndex());
        for (int i = firstIndex(); i > 0; i = nextIndex(i)) {
            String field = getFieldNameAt(i);
            if (field != null) {
                fields.add(field);
            }
        }
        return fields;
    }

    @Override
    public Collection<SchemaField> getSchemaFields() {
        List<SchemaField> schemaFields = new ArrayList<>(lastIndex());
        for (int i = firstIndex(); i > 0; i = nextIndex(i)) {
            schemaFields.add(getSchemaFieldAt(i));
        }
        return schemaFields;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataSchema) {
            return DataSchema.equals(this, (DataSchema) obj);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return DataSchema.hashCode(this);
    }

    @Override
    public String toString() {
        return DataSchema.toString(this);
    }

}
