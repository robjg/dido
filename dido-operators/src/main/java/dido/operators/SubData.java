package dido.operators;

import dido.data.NoSuchFieldException;
import dido.data.*;

import java.util.Collection;
import java.util.function.Function;

/**
 * Provide data that is a subset of some other data.
 */
public class SubData extends AbstractData implements DidoData {

    private final ReadableSchema dataSchema;

    private final int[] indices;

    private final IndexedData original;

    private SubData(ReadableSchema dataSchema, int[] indices, IndexedData original) {
        this.dataSchema = dataSchema;
        this.indices = indices;
        this.original = original;
    }

    public static Function<DidoData, DidoData> subDataOf(ReadableSchema original, int... indices) {

        SubSchemaFactory schemaFactory = new SubSchemaFactory(
                original, indices);

        for (int index : indices) {
            SchemaField originalField = original.getSchemaFieldAt(index);
            if (originalField == null) {
                throw new dido.data.NoSuchFieldException(index, original);
            }
            schemaFactory.addSchemaField(originalField.mapToIndex(0));
        }
        return new MappingFunc(schemaFactory.toSchema(), indices);
    }

    public static Function<DidoData, DidoData> subDataOf(ReadableSchema original, String... fields) {

        int[] indices = new int[fields.length];
        for (int i = 0; i < indices.length; ++i) {
            String field = fields[i];
            int index = original.getIndexNamed(field);
            if (index == 0) {
                throw new dido.data.NoSuchFieldException(field, original);
            }
            indices[i] = index;
        }
        return subDataOf(original, indices);
    }

    private static class MappingFunc implements Function<DidoData, DidoData> {

        private final int[] indices;

        private final SubSchema subSchema;

        private MappingFunc(SubSchema subSchema, int[] indices) {
            this.subSchema = subSchema;
            this.indices = indices;
        }

        @Override
        public DidoData apply(DidoData original) {

            return new SubData(subSchema, indices, original);
        }
    }

    static class SubSchemaFactory extends SchemaFactoryImpl<SubSchema> {

        private final ReadableSchema original;

        private final int[] indices;

        SubSchemaFactory(ReadableSchema original, int[] indices) {
            this.original = original;
            this.indices = indices;
        }

        @Override
        protected SubSchema create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            return new SubSchema(fields, firstIndex, lastIndex, original, indices);
        }
    }

    static class SubSchema extends DataSchemaImpl implements ReadableSchema {

        private final ReadableSchema original;

        private final int[] indices;

        SubSchema(Collection<SchemaField> fields, int firstIndex, int lastIndex,
                  ReadableSchema original, int[] indices) {
            super(fields, firstIndex, lastIndex);
            this.original = original;
            this.indices = indices;
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            int oldIndex = indices[index - 1];
            return original.getFieldGetterAt(oldIndex);
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, SubSchema.this);
            }
            return getFieldGetterAt(index);
        }
    }

    private static class SchemaUnknownMappingFunc implements Function<DidoData, DidoData> {

        private final int[] indices;

        private ReadableSchema lastSchema;

        private Function<DidoData, DidoData> mappingFunc;

        private SchemaUnknownMappingFunc(int[] indices) {
            this.indices = indices;
        }

        @Override
        public DidoData apply(DidoData original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();

                mappingFunc = subDataOf(lastSchema, indices);

            }
            return mappingFunc.apply(original);
        }
    }

    public static class SchemaUnknownFieldMappingFunc implements Function<DidoData, DidoData> {

        private final String[] fields;

        private ReadableSchema lastSchema;

        private Function<DidoData, DidoData> mappingFunc;

        private SchemaUnknownFieldMappingFunc(String[] fields) {
            this.fields = fields;
        }

        @Override
        public DidoData apply(DidoData original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();

                mappingFunc = subDataOf(lastSchema, fields);
            }

            return mappingFunc.apply(original);
        }
    }

    public static Function<DidoData, DidoData> ofIndices(int... indices) {

        return new SchemaUnknownMappingFunc(indices);
    }

    public static Function<DidoData, DidoData> ofFields(String... fields) {

        return new SchemaUnknownFieldMappingFunc(fields);
    }

    @Override
    public ReadableSchema getSchema() {
        return dataSchema;
    }

    @Override
    public Object getAt(int index) {
        return original.getAt(indices[index - 1]);
    }

    @Override
    public boolean hasIndex(int index) {
        return original.hasIndex(indices[index - 1]);
    }

}
