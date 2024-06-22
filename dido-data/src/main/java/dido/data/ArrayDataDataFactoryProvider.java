package dido.data;

/**
 * Provide an {@link DataFactory} based on {@link ArrayData}.
 */
public class ArrayDataDataFactoryProvider implements DataFactoryProvider<NamedData> {

    @Override
    public Class<NamedData> getDataType() {
        return NamedData.class;
    }

    @Override
    public DataFactory<NamedData> provideFactory(DataSchema schema) {



        return ArrayData.factoryFor(schema);

    }
}
