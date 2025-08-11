package dido.data.util;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.useful.AbstractData;

public class EmptyData extends AbstractData {

    private final DataSchema schema;

    private EmptyData(DataSchema schema) {
        this.schema = schema;
    }

    public static DidoData withSchema(DataSchema schema) {
        return new EmptyData(schema);
    }

    @Override
    public DataSchema getSchema() {
        return schema;
    }

    @Override
    public Object getAt(int index) {
        return null;
    }
}
