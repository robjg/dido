package dido.data;


import dido.data.useful.DataSchemaImpl;
import dido.data.useful.SchemaFactoryImpl;

import java.util.Collection;

/**
 * Creates a simple {@link DataSchema}.
 */
public class DataSchemaFactory extends SchemaFactoryImpl<DataSchema> implements SchemaFactory {

    @Override
    protected DataSchema create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
        return DataSchemaImpl.fromFields(fields, firstIndex, lastIndex);
    }

    private DataSchemaFactory() {
    }

    private DataSchemaFactory(DataSchema originalSchema) {
        super(originalSchema);
    }

    public static DataSchemaFactory newInstance() {
            return new DataSchemaFactory();
    }

    public static DataSchemaFactory newInstanceFrom(DataSchema originalSchema) {
        return new DataSchemaFactory(originalSchema);
    }
}
