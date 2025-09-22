package dido.data.immutable;

import dido.data.DataFactory;
import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.SchemaFactory;

/**
 * Provide an {@link DataFactory} based on {@link ArrayData}.
 */
public class ArrayDataDataFactoryProvider implements DataFactoryProvider {

    @Override
    public SchemaFactory getSchemaFactory() {
        return ArrayData.schemaFactory();
    }

    @Override
    public DataFactory factoryFor(DataSchema schema) {

        return ArrayData.factoryForSchema(ArrayData.asArrayDataSchema(schema));
    }
}
