package dido.data.generic;

import dido.data.AbstractDataSchema;
import dido.data.DataSchema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractGenericDataSchema<F> extends AbstractDataSchema
        implements GenericDataSchema<F> {

    @Override
    public F getFieldAt(int index) {
        GenericSchemaField<F> schemaField = getSchemaFieldAt(index);
        return schemaField == null ? null : schemaField.getField();
    }

    @Override
    public String getFieldNameAt(int index) {
        F field = getFieldAt(index);
        return field == null ? null : field.toString();
    }

    @Override
    public GenericSchemaField<F> getSchemaFieldNamed(String name) {
        int index = getIndexNamed(name);
        return index > 0 ? getSchemaFieldAt(index) : null;
    }

    @Override
    public GenericSchemaField<F> getSchemaFieldOf(F field) {
        int index = getIndexOf(field);
        return index > 0 ? getSchemaFieldAt(index) : null;
    }

    @Override
    public String getFieldNameOf(F field) {
        GenericSchemaField<F> genericSchemaField = getSchemaFieldOf(field);
        return genericSchemaField == null ? null : genericSchemaField.getName();
    }

    @Override
    public int getIndexOf(F field) {
        GenericSchemaField<F> genericSchemaField = getSchemaFieldOf(field);
        return genericSchemaField == null ? 0 : genericSchemaField.getIndex();
    }

    @Override
    public Class<?> getTypeOf(F field) {
        GenericSchemaField<F> schemaField = getSchemaFieldOf(field);
        return schemaField == null ? null : schemaField.getType();
    }

    @Override
    public DataSchema getSchemaOf(F field) {
        GenericSchemaField<F> schemaField = getSchemaFieldOf(field);
        return schemaField == null ? null : schemaField.getNestedSchema();
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

}
