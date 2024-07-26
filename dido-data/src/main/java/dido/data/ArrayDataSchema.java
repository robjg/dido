package dido.data;

import java.util.Collection;

/**
 * Schema for {@link ArrayData}.
 */
public class ArrayDataSchema extends DataSchemaImpl
        implements WritableSchema<ArrayData> {

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
    public Getter getDataGetterAt(int index) {
        return ArrayData.getDataGetterAt(index, this);
    }

    @Override
    public Getter getDataGetterNamed(String name) {
        return ArrayData.getDataGetterNamed(name, this);
    }

    @Override
    public WritableSchemaFactory<ArrayData> newSchemaFactory() {
        return new ArrayDataSchemaFactory();
    }

    @Override
    public DataFactory<ArrayData> newDataFactory() {
        return new ArrayData.ArrayDataFactory(this);
    }

    public static class ArrayDataSchemaFactory extends SchemaFactoryImpl<ArrayDataSchema>
        implements WritableSchemaFactory<ArrayData> {

        protected ArrayDataSchemaFactory() {
        }

        protected ArrayDataSchemaFactory(DataSchema from) {
            super(from);
        }

        @Override
        ArrayDataSchema create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            return new ArrayDataSchema(fields, firstIndex, lastIndex);
        }
    }

}
