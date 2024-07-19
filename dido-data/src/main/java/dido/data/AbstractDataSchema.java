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
    public SchemaField getSchemaFieldNamed(String name) {
        int index = getIndexNamed(name);
        return index > 0 ? getSchemaFieldAt(index) : null;
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
    public Getter getDataGetterAt(int index) {
        String toString = "DataGetter for [" + index + ":" + getFieldNameAt(index) + "]";
        return new AbstractGetter() {
            @Override
            public Object get(DidoData data) {
                return data.getAt(index);
            }

            @Override
            public String toString() {
                return toString;
            }
        };
    }

    @Override
    public Getter getDataGetterNamed(String name) {
        String toString = "DataGetter for [" + getIndexNamed(name) + ":" + name + "]";
        return new AbstractGetter() {
            @Override
            public Object get(DidoData data) {
                return data.getNamed(name);
            }

            @Override
            public String toString() {
                return toString;
            }
        };
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
