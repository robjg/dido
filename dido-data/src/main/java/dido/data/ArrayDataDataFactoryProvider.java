package dido.data;

/**
 * Provide an {@link DataFactory} based on {@link ArrayData}.
 */
public class ArrayDataDataFactoryProvider implements DataFactoryProvider<ArrayData> {

    @Override
    public Class<ArrayData> getDataType() {
        return ArrayData.class;
    }

    @Override
    public WritableSchemaFactory<ArrayData> getSchemaFactory() {
        return ArrayData.schemaFactory();
    }

    @Override
    public DataFactory<ArrayData> provideFactory(DataSchema schema) {

        return ArrayData.factoryFor(ArrayData.asArrayDataSchema(schema));
    }
}
