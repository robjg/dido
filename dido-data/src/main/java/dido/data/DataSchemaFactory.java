package dido.data;


/**
 * Creates a simple {@link DataSchema}.
 */
public class DataSchemaFactory extends SchemaFactoryImpl<DataSchema> implements SchemaFactory<DataSchema> {

    private DataSchemaFactory() {
        super(DataSchemaImpl::fromFields);
    }

    public static DataSchemaFactory newInstance() {
            return new DataSchemaFactory();
    }
}
