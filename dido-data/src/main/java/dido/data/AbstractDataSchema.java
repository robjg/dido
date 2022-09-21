package dido.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractDataSchema<F> implements DataSchema<F> {

    @Override
    public F getFieldAt(int index) {

        return getSchemaFieldAt(index).getField();
    }

    @Override
    public Class<?> getTypeAt(int index) {
        return getSchemaFieldAt(index).getType();
    }

    @Override
    public <N> DataSchema<N> getSchemaAt(int index) {
        return getSchemaFieldAt(index).getNestedSchema();
    }

    @Override
    public Collection<F> getFields() {
        List<F> fields = new ArrayList<>(lastIndex());
        for (int i = firstIndex(); i > 0; i = nextIndex(i)) {
            F field = getFieldAt(i);
            if (field != null) {
                fields.add(field);
            }
        }
        return fields;
    }

    @Override
    public Collection<SchemaField<F>> getSchemaFields() {
        List<SchemaField<F>> schemaFields = new ArrayList<>(lastIndex());
        for (int i = firstIndex(); i > 0; i = nextIndex(i)) {
            schemaFields.add(getSchemaFieldAt(i));
        }
        return schemaFields;
    }

    public SchemaField<F> getSchemaField(F field) {
        int index = getIndex(field);
        return index > 0 ? getSchemaFieldAt(index) : null;
    }

    @Override
    public Class<?> getType(F field) {
        SchemaField<F> schemaField = getSchemaField(field);
        return schemaField == null ? null : schemaField.getType();
    }

    @Override
    public <N> DataSchema<N> getSchema(F field) {
        SchemaField<F> schemaField = getSchemaField(field);
        return schemaField == null ? null : schemaField.getNestedSchema();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataSchema) {
            return DataSchema.equals(this, (DataSchema<?>) obj);
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
