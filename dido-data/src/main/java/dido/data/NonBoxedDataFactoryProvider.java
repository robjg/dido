package dido.data;

/**
 * Provide an {@link DataFactory} based on {@link NonBoxedData}.
 */
public class NonBoxedDataFactoryProvider implements DataFactoryProvider {

    @Override
    public SchemaFactory getSchemaFactory() {
        return NonBoxedData.schemaFactory();
    }

    @Override
    public DataFactory factoryFor(DataSchema schema) {
        return NonBoxedData.factoryForSchema(schema);
    }
}
