package dido.data.generic;

import dido.data.DataSchema;
import dido.data.useful.AbstractDataSchema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractGenericDataSchema<F> extends AbstractDataSchema
        implements GenericDataSchema<F> {

    @Override
    abstract public Class<F> getFieldType();

    @Override
    abstract public GenericSchemaField<F> getSchemaFieldOf(F field);

    @Override
    abstract public GenericSchemaField<F> getSchemaFieldAt(int index);

    @Override
    abstract public GenericSchemaField<F> getSchemaFieldNamed(String name);

    @Override
    public boolean hasField(F field) {
        return  getSchemaFieldOf(field) != null;
    }

    @Override
    public F getFieldNamed(String name) {
        GenericSchemaField<F> schemaField = getSchemaFieldNamed(name);
        return schemaField == null ? null : schemaField.getField();
    }

    @Override
    public F getFieldAt(int index) {
        GenericSchemaField<F> schemaField = getSchemaFieldAt(index);
        return schemaField == null ? null : schemaField.getField();
    };

    @Override
    public String getFieldNameAt(int index) {
        GenericSchemaField<F> schemaField = getSchemaFieldAt(index);
        return schemaField == null ? null : schemaField.getName();
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

    @Override
    public Collection<GenericSchemaField<F>> getGenericSchemaFields() {
        List<GenericSchemaField<F>> schemaFields = new ArrayList<>(lastIndex());
        for (int i = firstIndex(); i > 0; i = nextIndex(i)) {
            schemaFields.add(getSchemaFieldAt(i));
        }
        return schemaFields;
    }

}
