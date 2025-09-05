package dido.data.mutable;

import dido.data.*;
import dido.data.NoSuchFieldException;
import dido.data.schema.DataSchemaFactory;
import dido.data.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * An implementation of {@link MalleableData} backed by an Array.
 */
public class MalleableArrayData extends AbstractMalleableData implements MalleableData {

    private final int initialCapacity;

    private Object[] values;

    private final SchemaFactory schemaFactory = DataSchemaFactory.newInstance();

    public MalleableArrayData() {
        this(32);
    }

    public MalleableArrayData(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        this.values = new Object[initialCapacity];
    }

    public static MalleableArrayData copy(DidoData data) {
        MalleableArrayData copy = new MalleableArrayData(data.getSchema().lastIndex());
        for (SchemaField schemaField : data.getSchema().getSchemaFields()) {
            copy.setField(schemaField, data.getAt(schemaField.getIndex()));
        }
        return copy;
    }

    @Override
    public DataSchema getSchema() {
        return schemaFactory;
    }

    @Override
    public Object getAt(int index) {
        return values[index - 1];
    }

    @Override
    public void clearAt(int index) {
        values[index - 1] = null;
    }

    @Override
    public void clearNamed(String name) {
        int index = getSchema().getIndexNamed(name);
        if (index > 0) {
            clearAt(index);
        }
        else {
            throw new NoSuchFieldException(name, getSchema());
        }
    }

    @Override
    public void removeAt(int index) {

        clearAt(index);
        schemaFactory.removeAt(index);
    }

    @Override
    public void removeNamed(String name) {

        clearNamed(name);
        schemaFactory.removeNamed(name);
    }

    @Override
    public void setAt(int index, Object value, Type type) {
        if (index < 1) {
            throw new IllegalArgumentException("Index must be > 0");
        }
        SchemaField schemaField = schemaFactory.getSchemaFieldAt(index);
        if (schemaField != null && !TypeUtil.isAssignableFrom(schemaField.getType(), type)) {
            schemaField = schemaField.withType(type);
        }

        if (schemaField == null) {
            schemaField = SchemaField.of(index, null, type);
        }

        setField(schemaField, value);
    }

    @Override
    public void setNamed(String name, Object value, Type type) {

        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }

        SchemaField schemaField = schemaFactory.getSchemaFieldNamed(name);

        if (schemaField != null && !TypeUtil.isAssignableFrom(schemaField.getType(), type)) {
            schemaField = schemaField.withType(type);
        }

        if (schemaField == null) {
            schemaField = SchemaField.of(0, name, type);
        }

        setField(schemaField, value);
    }

    @Override
    public void setNamedAt(int index, String name, Object value, Type type) {

        SchemaField existing = null;
        if (name != null) {
            existing = schemaFactory.getSchemaFieldNamed(name);
            // Change of index for name.
            if (existing != null && existing.getIndex() != index) {
                removeNamed(name);
                existing = null;
            }
        }
        if (existing == null && index > 0) {
            existing = schemaFactory.getSchemaFieldAt(index);
            if (existing != null && name != null) {
                existing = existing.mapToFieldName(name);
            }
        }

        SchemaField newField = null;
        if (existing != null && !TypeUtil.isAssignableFrom(existing.getType(), type)) {
            newField = existing.withType(type);
        }
        if (newField == null) {
            newField = SchemaField.of(index, name, type);
        }

        setField(newField, value);
    }

    @Override
    public void setField(SchemaField schemaField, Object value) {

        schemaField = schemaFactory.addSchemaField(schemaField);
        int index = schemaField.getIndex();
        ensure(index)[index - 1] = value;
    }


    private Object[] ensure(int minCapacity) {
        if (minCapacity <= values.length) {
            return values;
        }
        int newCapacity = values.length;
        while (newCapacity < minCapacity) {
            newCapacity += initialCapacity;
        }
        return values = Arrays.copyOf(values,
                newCapacity);
    }

}
