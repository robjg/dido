package dido.operators;

import dido.data.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Provide an alternative view of data by changing fields.
 *
 * @param <F>
 */
public class AlternativeView<F> implements Function<IndexedData<F>, GenericData<F>> {

    private final Map<F, F> fromToFields;

    private final Map<F, F> toFromFields;

    private DataSchema<F> lastSchema;

    private DataSchema<F> newSchema;

    private AlternativeView(Map<F, F> fromToFields, Map<F, F> toFromFields) {
        this.fromToFields = fromToFields;
        this.toFromFields = toFromFields;
    }

    public static class With<F> {

        private final Map<F, F> fieldMap = new HashMap<>();

        public With<F> fieldChange(F original, F toField) {
            fieldMap.put(original, toField);
            return this;
        }

        public Function<IndexedData<F>, GenericData<F>> make() {

            final Map<F, F> fromToFields = new HashMap<>();

            final Map<F, F> toFromFields = new HashMap<>();

            for (Map.Entry<F, F> entry : this.fieldMap.entrySet()) {
                fromToFields.put(entry.getKey(), entry.getValue());
                toFromFields.put(entry.getValue(), entry.getKey());
            }

            return new AlternativeView<>(fromToFields, toFromFields);
        }
    }

    public static <F> With<F> with() {
        return new With<>();
    }

    @Override
    public GenericData<F> apply(IndexedData<F> originalData) {

        GenericData<F> inData = GenericData.from(originalData);

        DataSchema<F> originalSchema = inData.getSchema();
        if (lastSchema == null || !lastSchema.equals(originalSchema)) {
            newSchema = new Schema(originalSchema);
            lastSchema = originalSchema;
        }

        return new Data(inData, newSchema);
    }

    class Schema extends AbstractDataSchema<F> {

        private final DataSchema<F> original;

        Schema(DataSchema<F> original) {
            this.original = original;
        }

        @Override
        public SchemaField<F> getSchemaFieldAt(int index) {
            SchemaField<F> original = this.original.getSchemaFieldAt(index);
            F now = fromToFields.get(original.getField());
            if (now == null) {
                return original;
            }
            else {
                return original.mapToField(now);
            }
        }

        @Override
        public int getIndex(F field) {
            F original = toFromFields.get(field);
            if (original == null) {
                return this.original.getIndex(field);
            }
            else {
                return this.original.getIndex(original);
            }
        }

        @Override
        public int firstIndex() {
            return original.firstIndex();
        }

        @Override
        public int nextIndex(int index) {
            return original.nextIndex(index);
        }

        @Override
        public int lastIndex() {
            return original.lastIndex();
        }
    }


    class Data extends AbstractGenericData<F> {

        private final GenericData<F> originalData;

        private final DataSchema<F> schema;

        Data(GenericData<F> originalData, DataSchema<F> schema) {
            this.originalData = originalData;
            this.schema = schema;
        }


        @Override
        public DataSchema<F> getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            return originalData.getAt(index);
        }

        @Override
        public boolean hasIndex(int index) {
            return originalData.hasIndex(index);
        }
    }
}
