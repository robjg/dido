package dido.data;

/**
 * Provide an {@link DataFactory} based on {@link MapData}.
 */
public class MapDataDataFactoryProvider implements DataFactoryProvider<NamedData> {

    @Override
    public Class<NamedData> getDataType() {
        return NamedData.class;
    }

    @Override
    public DataFactory<NamedData> provideFactory(DataSchema schema) {

        return MapData.factoryFor(schema);
    }
}
