package dido.operators;

import dido.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Flatten {

    public static <F> List<GenericData<F>> flatten(F field, IndexedData<F> data) {
        return new DynamicFlatten<>(field).apply(data);
    }


    public static <F> Function<IndexedData<F>, List<GenericData<F>>> field(F field) {

        return new DynamicFlatten<>(field);
    }

    public static <F> Function<IndexedData<F>, List<GenericData<F>>> fieldOfSchema(F field, DataSchema<F> schema) {

        DataSchema<F> nestedSchema = Objects.requireNonNull(schema.getSchema(field),
                "No Nested Schema for Field [" + field + "]");

        Concatenator<F> concatenator = Concatenator.<F>withSettings()
                .excludeFields(field)
                .makeFromSchemas(schema, nestedSchema);

        return new KnownFlatten<>(concatenator, field);
    }

    static class KnownFlatten <F> implements Function<IndexedData<F>, List<GenericData<F>>> {

        private final Concatenator<F> concatenator;

        private final F field;

        KnownFlatten(Concatenator<F> concatenator, F field) {
            this.concatenator = concatenator;
            this.field = field;
        }

        @Override
        public List<GenericData<F>> apply(IndexedData<F> data) {

            GenericData<F> genericData = GenericData.from(data);

            @SuppressWarnings("unchecked")
            RepeatingData<F> nested = (RepeatingData<F>) genericData.get(field);

            List<GenericData<F>> flattened = new ArrayList<>(nested.size());
            for (IndexedData<F> element : nested) {
                flattened.add(concatenator.concat(data, element));
            }

            return flattened;
        }
    }

    /**
     * Compares previous schemas so we can maybe shortcut.
     *
     * @param <F> Field Type.
     */
    public static class DynamicFlatten<F> implements Function<IndexedData<F>, List<GenericData<F>>> {

        private final F field;

        private Function<IndexedData<F>, List<GenericData<F>>> last;

        private DataSchema<F> previous;

        public DynamicFlatten(F field) {
            this.field = field;
        }

        @Override
        public List<GenericData<F>> apply(IndexedData<F> indexedData) {

            if (last == null || !indexedData.getSchema().equals(previous)) {
                previous = indexedData.getSchema();
                last = fieldOfSchema(field, previous);
            }

            return last.apply(indexedData);
        }
    }
}
