package dido.data;

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
