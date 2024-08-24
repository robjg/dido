package dido.operators.transform;

import dido.data.NoSuchFieldException;
import dido.data.*;

import java.util.Objects;
import java.util.function.Function;

public class FieldOperations {

    /**
     * Create an operation that copies the field at an index.
     *
     * @param index The index to copy.
     * @return An Copy Operation Definition.
     */
    public static FieldOperationDefinition copyAt(int index) {

        return (incomingSchema, schemaSetter) -> {

            Getter getter = incomingSchema.getDataGetterAt(index);

            SchemaField originalField = incomingSchema.getSchemaFieldAt(index);

            if (originalField == null) {
                throw new NoSuchFieldException(index, incomingSchema);
            }

            SchemaField newField = originalField.mapToIndex(0);

            schemaSetter.addSchemaField(newField);

            return dataFactory ->
                    new Copy(getter, dataFactory.getSetterNamed(originalField.getName()));
        };
    }

    public static FieldOperationDefinition copyNamed(String from, String to) {

        Objects.requireNonNull(from, "From");
        String finalTo = Objects.requireNonNullElse(to, from);

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(from)
                    .mapTo(0, finalTo);

            schemaSetter.addSchemaField(field);

            Getter getter = incomingSchema.getDataGetterNamed(from);

            return dataFactory -> new Copy(getter, dataFactory.getSetterNamed(finalTo));
        };
    }

    public static <T> FieldOperationDefinition computeNamed(String to,
                                                            Class<T> type,
                                                            Function<? super DidoData, T> func) {

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(to);
            if (field == null) {
                field = SchemaField.of(0, to, type);
            }
            schemaSetter.addSchemaField(field);

            return dataFactory -> new Compute(dataFactory.getSetterNamed(to), func);
        };
    }

    public static FieldOperationDefinition removeNamed(String name) {

        return (incomingSchema, schemaSetter) -> {
            SchemaField field = incomingSchema.getSchemaFieldNamed(name);
            if (field == null) {
                throw new NoSuchFieldException(name, incomingSchema);
            }
            schemaSetter.removeNamed(field);
            return dataFactory -> dataIn -> {
            };
        };
    }

    static class Copy implements FieldOperation {

        private final Getter getter;

        private final Setter setter;

        Copy(Getter getter, Setter setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public void accept(DidoData data) {
            if (getter.has(data)) {
                setter.set(getter.get(data));
            } else {
                setter.clear();
            }
        }
    }


    static class Compute implements FieldOperation {

        private final Setter setter;

        private final Function<? super DidoData, ?> func;

        Compute(Setter setter, Function<? super DidoData, ?> func) {
            this.setter = setter;
            this.func = func;
        }

        @Override
        public void accept(DidoData data) {
            Object now = func.apply(data);
            if (now == null) {
                setter.clear();
            } else {
                setter.set(now);
            }
        }
    }

}
