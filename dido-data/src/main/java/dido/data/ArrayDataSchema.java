package dido.data;

/**
 * Schema for {@link ArrayData}.
 */
public class ArrayDataSchema extends DataSchemaImpl
        implements TransformableSchema<ArrayData, ArrayDataSchema> {

    ArrayDataSchema() {
    }

    ArrayDataSchema(DataSchema from) {
        super(from.getSchemaFields(), from.firstIndex(), from.lastIndex());
    }

    ArrayDataSchema(Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex) {
        super(schemaFields, firstIndex, lastIndex);
    }

    public static SchemaBuilder<ArrayDataSchema> newBuilder() {
        return ArrayData.schemaBuilder();
    }

    @Override
    public SchemaFactory<ArrayDataSchema> newSchemaFactory() {
        return new ArrayDataSchemaFactory();
    }

    @Override
    public DataFactory<ArrayData> newDataFactory() {
        return new ArrayData.ArrayDataFactory(this);
    }

    public static class ArrayDataSchemaFactory extends SchemaFactoryImpl<ArrayDataSchema> {

        protected ArrayDataSchemaFactory() {
            super(ArrayDataSchema::new);
        }

        protected ArrayDataSchemaFactory(DataSchema from) {
            super(ArrayDataSchema::new, from);
        }
    }

    @Override
    public Getter getDataGetterAt(int index) {
        return ArrayData.getDataGetterAt(index, this);
    }

    @Override
    public Getter getDataGetterNamed(String name) {
        return ArrayData.getDataGetterNamed(name, this);
    }
}
