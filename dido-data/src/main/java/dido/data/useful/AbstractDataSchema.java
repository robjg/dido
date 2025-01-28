package dido.data.useful;

import dido.data.DataSchema;
import dido.data.SchemaField;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base class providing default implementations of {@link DataSchema}.
 */
public abstract class AbstractDataSchema implements DataSchema {

    @Override
    abstract public int firstIndex();

    @Override
    abstract public int nextIndex(int index);

    @Override
    abstract public int lastIndex();

    @Override
    abstract public SchemaField getSchemaFieldAt(int index);

    @Override
    abstract public SchemaField getSchemaFieldNamed(String name);

    @Override
    public boolean hasIndex(int index) {
        return getSchemaFieldAt(index) != null;
    }

    @Override
    public boolean hasNamed(String name) {
        return getSchemaNamed(name) != null;
    }

    @Override
    public String getFieldNameAt(int index) {
        SchemaField schemaField = getSchemaFieldAt(index);
        return schemaField == null ? null : schemaField.getName();
    }

    @Override
    public Type getTypeAt(int index) {
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
    public Type getTypeNamed(String name) {
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
