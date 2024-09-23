package dido.data;

/**
 * Provide an {@link DataFactory} based on {@link MapData}.
 */
public class MapDataDataFactoryProvider implements DataFactoryProvider<MapData> {

    @Override
    public Class<MapData> getDataType() {
        return MapData.class;
    }

    @Override
    public WritableSchemaFactory<MapData> getSchemaFactory() {
        return MapData.schemaFactory();
    }

    @Override
    public DataFactory<MapData> provideFactory(DataSchema schema) {
        return MapData.factoryFor(schema);
    }
}
