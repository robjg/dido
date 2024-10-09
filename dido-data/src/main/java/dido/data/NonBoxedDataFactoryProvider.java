package dido.data;

/**
 * Provide an {@link DataFactory} based on {@link NonBoxedData}.
 */
public class NonBoxedDataFactoryProvider implements DataFactoryProvider<NonBoxedData> {

    @Override
    public Class<NonBoxedData> getDataType() {
        return NonBoxedData.class;
    }

    @Override
    public SchemaFactory getSchemaFactory() {
        return NonBoxedData.schemaFactory();
    }

    @Override
    public DataFactory<NonBoxedData> provideFactory(DataSchema schema) {
        return NonBoxedData.factoryForSchema(schema);
    }
}
