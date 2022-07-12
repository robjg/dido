package dido.data;

import java.util.function.Function;

/**
 * Provide data that is a subset of some other data.
 *
 * @param <F> The field type.
 */
public class SubData<F> extends AbstractGenericData<F> implements GenericData<F> {

    private final DataSchema<F> dataSchema;

    private final int[] indices;
    private final IndexedData<F> original;


    private SubData(DataSchema<F> dataSchema, int[] indices, IndexedData<F> original) {
        this.dataSchema = dataSchema;
        this.indices = indices;
        this.original = original;
    }

    public static class MappingFunc<F> implements Function<IndexedData<F>, GenericData<F>> {

        private final int[] indices;

        private DataSchema<F> lastSchema;

        private DataSchema<F> subSchema;
        private MappingFunc(int[] indices) {
            this.indices = indices;
        }

        @Override
        public GenericData<F> apply(IndexedData<F> original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();
                SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();
                for (int i = 0; i < indices.length; ++i) {
                    schemaBuilder.add(lastSchema.getTypeAt(indices[i]));
                }
                subSchema = schemaBuilder.build();
            }
            return new SubData<>(subSchema, indices, original);
        }
    }

    public static <F> MappingFunc<F> ofIndices(int... indices) {

        return new MappingFunc<>(indices);
    }

    @Override
    public DataSchema<F> getSchema() {
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

    @Override
    public int hashCode() {
        return IndexedData.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IndexedData) {
            return IndexedData.equals(this, (IndexedData<?>) obj);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return IndexedData.toString(this);
    }

}
