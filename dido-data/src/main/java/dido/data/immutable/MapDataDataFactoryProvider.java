package dido.data.immutable;

import dido.data.DataFactory;
import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.SchemaFactory;

/**
 * Provide an {@link DataFactory} based on {@link MapData}.
 */
public class MapDataDataFactoryProvider implements DataFactoryProvider {

    @Override
    public SchemaFactory getSchemaFactory() {
        return MapData.schemaFactory();
    }

    @Override
    public DataFactory factoryFor(DataSchema schema) {
        return MapData.factoryForSchema(schema);
    }
}
