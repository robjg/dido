package dido.data.mutable;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.NoSuchFieldException;
import dido.data.SchemaField;

/**
 * An implementation of {@link MutableData} backed by an Array.
 */
public class MutableArrayData extends AbstractMutableData implements MutableData {

    private final Object[] values;

    private final DataSchema schema;

    public MutableArrayData(DataSchema schema) {
        this.schema = schema;
        this.values = new Object[schema.lastIndex()];
    }

    public static MutableArrayData copy(DidoData data) {
        MutableArrayData copy = new MutableArrayData(data.getSchema());
        for (SchemaField schemaField : data.getSchema().getSchemaFields()) {
            copy.setNamed(schemaField.getName(), data.getAt(schemaField.getIndex()));
        }
        return copy;
    }

    @Override
    public DataSchema getSchema() {
        return schema;
    }

    @Override
    public Object getAt(int index) {
        try {
            return values[index - 1];
        }
        catch (IndexOutOfBoundsException e) {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public void clearAt(int index) {
        setAt(index, null);
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
    public void setAt(int index, Object value) {
        try {
            values[index -1] = value;
        }
        catch (IndexOutOfBoundsException e) {
            throw new NoSuchFieldException(index, schema);
        }
    }
}
